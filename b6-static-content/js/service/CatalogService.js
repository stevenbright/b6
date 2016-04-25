'use strict';

import ajax from 'rsvp-ajax';
import cache from 'rsvp-cache';
import {Promise} from 'rsvp';

import {DEFAULT_LIMIT} from '../util/Constants';

function prepareRequestWithOffsetAndLimit(offsetToken, limit) {
  limit = limit || DEFAULT_LIMIT;

  var request = {
    "limit": limit
  };

  if (offsetToken) {
    request["offsetToken"] = offsetToken;
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
//  getItemByType(itemTypeId: number, offsetToken: string, limit: number): Promise {
//    const request = prepareRequestWithOffsetAndLimit(offsetToken, limit);
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
