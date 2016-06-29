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
  return ajax.request('POST', '/rest/rpc/CatalogService/' + methodName, requestBody);
}

class CatalogService {

  getItem(id: string): Promise {
    return exec('getItem', {'id': id});
  }

  getItems(request): Promise {
    console.log('getItems : request =', request);
    return exec('getItems', request);
  }

  setFavorite(id: string, isFavorite: boolean): Promise {
    return exec('setFavorite', {'itemId': id, 'isFavorite': isFavorite});
  }

  getFavoriteItems(request): Promise {
    console.log('getFavoriteItems : request =', request);
    return exec('getFavoriteItems', request);
  }
};

export default new CatalogService();
