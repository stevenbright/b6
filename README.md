
# Sample REST Calls

## Health Check

```
curl -u testonly:test -X POST 127.0.0.1:8080/rest/health
```

Should result in

```
OK
```

## Fetch Server Configuration

```
curl -u testonly:test -X POST 127.0.0.1:8080/g/admin/config
```

## Lookup

### Item

```
curl -vv -X POST -H 'Content-Type: application/json' -H 'Accept: application/json' 127.0.0.1:8080/rest/CatalogRestService?m=getCatalogItem -d '{"id": 12}'
```


OLD:
```
curl -u testonly:test -H 'Accept: application/json; charset=UTF-8' -H 'Content-Type: application/json; charset=UTF-8' -X GET 127.0.0.1:8080/rest/eolaire/item/150 -s | python -mjson.tool
```

# Client-side Development

## Open Demo Page

Try ```${path}/index.html?mode=0#/demo``` and ```${path}/index.html?mode=1#/demo```, where path points to where index.html
is served from (can be local or deployed somewhere) and ``mode`` designates a page, that will be served.

## Start Watcher

```
./node_modules/grunt-cli/bin/grunt watch
```

## Known Issues

### OS X Grunt Build Failure

Increase ``ulimit`` if you get ``>>> Error: EMFILE ...``.

```
ulimit -n 512
```

## Sample Configs

Use sample VM properties: ``-Dbrikar.settings.path=file:/home/user/opt/config/b6.properties``.

Use ``core.properties`` as a base.

## Links

TBD
TBD




