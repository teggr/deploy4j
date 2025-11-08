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

Available: http://localhost:4000/deploy4j/

# References

https://github.com/envygeeks/jekyll-docker/blob/master/README.md
https://bulma.io/documentation/elements/content/
https://jekyllrb.com/docs/configuration/sass/
https://docs.github.com/en/pages/setting-up-a-github-pages-site-with-jekyll/creating-a-github-pages-site-with-jekyll