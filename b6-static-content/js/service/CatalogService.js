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

class CatalogService {

  getItemById(id: number): Promise {
    //return ajax.request("GET", toUrl("/item/entry/" + id));
    return null;
  }

//  getItemProfile(id: number): Promise {
//    return ajax.request("GET", toUrl("/item/profile/" + id));
//  }
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
