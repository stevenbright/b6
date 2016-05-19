'use strict';

import {DEFAULT_LIMIT} from '../util/Constants';

function prepareRequestWithCursorAndLimit(cursor, limit) {
  limit = limit || DEFAULT_LIMIT;

  var request = {
    "limit": limit
  };

  if (cursor) {
    request["cursor"] = cursor;
  }

  return request;
}

function exec(methodName, requestBody) {
  return ajax.request('POST', '/rest/CatalogRestService?m=' + methodName, requestBody);
}

class CatalogService {

  getItem(id: string): Promise {
    return exec('getItem', {'id': id});
  }

  getItems(cursor: string, limit: number): Promise {
    cursor = cursor || null;
    limit = limit || 0;
    return exec('getItems', {'cursor': cursor, 'limit': limit})
  }

//
//  getItemByType(itemTypeId: number, cursor: string, limit: number): Promise {
//    const request = prepareRequestWithCursorAndLimit(cursor, limit);
//    request.itemTypeId = itemTypeId;
//    return ajax.request("POST", toUrl("/item/query/by-type"), request);
//  }
//
//  getItemRelations(itemId: number, filterMode: string): Promise {
//    return ajax.request("POST", toUrl("/item/relations"), {
//      "itemId": itemId,
//      "relationsFilterMode": filterMode
//    });
//  }
};

export default new CatalogService();
