@echo off
setlocal enabledelayedexpansion

rem Parse arguments: -Version <ver> [-Push] [-NextSnapshot <ver>]
set "VERSION="
set "PUSH=0"
set "NEXTSNAPSHOT="
:parse
if "%~1"=="" goto after_parse
if /I "%~1"=="-Version" (
  shift
  if "%~1"=="" (
    echo ERROR: -Version requires a value
    exit /b 1
  )
  set "VERSION=%~1"
  shift
  goto parse
)
if /I "%~1"=="-Push" (
  set "PUSH=1"
  shift
  goto parse
)
if /I "%~1"=="-NextSnapshot" (
  shift
  if "%~1"=="" (
    echo ERROR: -NextSnapshot requires a value
    exit /b 1
  )
  set "NEXTSNAPSHOT=%~1"
  shift
  goto parse
)
echo ERROR: Unknown argument %~1
exit /b 1

:after_parse
if "%VERSION%"=="" (
  echo ERROR: Version is required. Usage: %~nx0 -Version 1.2.3 [-Push] [-NextSnapshot 1.2.4-SNAPSHOT]
  exit /b 1
)

set "ERRMSG="
rem Ensure we're in a git repo and working tree is clean
for /f "delims=" %%b in ('git rev-parse --abbrev-ref HEAD 2^>nul') do set "BRANCH=%%b"
if not defined BRANCH (
  echo ERROR: not a git repository
  exit /b 1
)
set "STATUS="
for /f "delims=" %%s in ('git status --porcelain') do set "STATUS=%%s"
if defined STATUS (
  echo ERROR: working tree is not clean. Commit or stash changes first.
  exit /b 1
)

echo On branch: %BRANCH%
echo Setting Maven version to %VERSION% ...

mvn -DskipTests -DprocessAllModules=true versions:set -DnewVersion=%VERSION% -DgenerateBackupPoms=false
if errorlevel 1 (
  echo ERROR: maven versions:set failed
  exit /b 1
)

git add -A
set "CHANGES="
for /f "delims=" %%c in ('git status --porcelain') do set "CHANGES=%%c"
if defined CHANGES (
  git commit -m "chore(release): set version to %VERSION% for jitpack" || (
    echo ERROR: git commit failed
    exit /b 1
  )
  echo Committed version changes.
) else (
  echo No pom changes detected to commit.
)

set "TAG=v%VERSION%"
git rev-parse -q --verify refs/tags/%TAG% >nul 2>&1
if %errorlevel%==0 (
  echo ERROR: tag %TAG% already exists
  exit /b 1
)

git tag -a %TAG% -m "jitpack release %TAG%" || (
  echo ERROR: git tag failed
  exit /b 1
)
echo Created tag %TAG%

if "%PUSH%"=="1" (
  echo Pushing commit and tag to origin...
  git push origin HEAD || (echo ERROR: git push failed & exit /b 1)
  git push origin %TAG% || (echo ERROR: git push tag failed & exit /b 1)
  echo Push complete.
) else (
  echo Skipping push. To push, re-run with -Push.
)

if defined NEXTSNAPSHOT (
  echo Setting Maven version to next snapshot: %NEXTSNAPSHOT% ...
  mvn -DskipTests -DprocessAllModules=true versions:set -DnewVersion=%NEXTSNAPSHOT% -DgenerateBackupPoms=false
  if errorlevel 1 (
    echo ERROR: maven versions:set for next snapshot failed
    exit /b 1
  )

  git add -A
  set "CHANGES2="
  for /f "delims=" %%d in ('git status --porcelain') do set "CHANGES2=%%d"
  if defined CHANGES2 (
    git commit -m "chore: bump version to %NEXTSNAPSHOT%" || (
      echo ERROR: git commit failed
      exit /b 1
    )
    if "%PUSH%"=="1" (
      git push origin HEAD || (echo ERROR: git push failed & exit /b 1)
      echo Pushed snapshot bump.
    ) else (
      echo Snapshot bump committed locally. Re-run with -Push to push.
    )
  ) else (
    echo No snapshot changes detected to commit.
  )
)

echo Done. JitPack tag: %TAG%
endlocal
exit /b 0
