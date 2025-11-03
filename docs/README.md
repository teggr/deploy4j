# deploy4j documentation

## Creating docs folder using jekyll

```shell
set site_name=docs
docker run --rm --volume="%CD%:/srv/jekyll" -it jekyll/jekyll sh -c "chown -R jekyll /usr/gem/ && jekyll new %site_name%" && cd %site_name%
```

Uncommented for gh-pages as described in the `Gemfile` comments.

## Run as server

```shell
cd docs
docker run --rm --volume="%CD%:/srv/jekyll:Z" --volume="%CD%\vendor\bundle:/usr/local/bundle:Z" --publish [::1]:4000:4000 jekyll/jekyll jekyll serve --force-polling
```

## Jekyll theme

[Minima](https://github.com/jekyll/minima/blob/2.5-stable/README.md)