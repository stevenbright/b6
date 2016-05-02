
# Sample Urls

* ``http://127.0.0.1:8000/?mode=2#/demo``

# Sample Configs

Use sample VM properties: ``-Dbrikar.settings.path=file:/home/user/opt/config/b6.properties``.

Use ``core.properties`` as a base.

# Protobuf Session

```
var c = require('./target/generated/b6-catalog.js')

new c.b6.catalog.CatalogItem({id: 1, name: 'Test', type: 'book'})
```


For example, ``c.b6.catalog.GetCatalogItemReply.decode64('Cg4IDBIEbmFtZRoEYm9vaw==')`` results in:

```
{ item:
   { id: Long { low: 12, high: 0, unsigned: false },
     name: 'name',
     type: 'book' } }
```
