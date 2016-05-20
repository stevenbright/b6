'use strict';

import {DEFAULT_LIMIT} from '../util/Constants';

import ajax from '../util/ajax';


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

    const request = {'limit': limit};
    if (cursor != null) {
      request['cursor'] = cursor;
    }

    return exec('getItems', request);
  }
};

export default new CatalogService();
