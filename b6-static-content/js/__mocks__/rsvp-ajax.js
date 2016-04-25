'use strict';

import {Promise} from 'rsvp';

let REQUEST_MOCKS = { /* method: { url: {result: <mockData>, fn: <function>} }... */ }

function mockRequest(method, url, data) {
  if (REQUEST_MOCKS.hasOwnProperty(method)) {
    const urlMap = REQUEST_MOCKS[method];
    if (urlMap.hasOwnProperty(url)) {
      const resultData = urlMap[url];
      if (resultData.hasOwnProperty("result")) {
        return new Promise((resolve, _) => { resolve(resultData["result"]); });
      } else if (resultData.hasOwnProperty("fn")) {
        return new Promise((resolve, _) => { resolve(resultData["fn"](data)); });
      }
    }
  }

  const stubXMLHttpRequest = {
    DONE: "DONE",
    respone: "Not Found",
    responseType: "text",
    readyState: "DONE",
    status: 404
  };
  return new Promise((_, reject) => { reject(stubXMLHttpRequest); });
}

function __putMockResponse(method, url, result) {
  if (!REQUEST_MOCKS.hasOwnProperty(method)) {
    REQUEST_MOCKS[method] = {};
  }

  const methodMap = REQUEST_MOCKS[method];
  if (!methodMap.hasOwnProperty(url)) {
    methodMap[url] = {};
  }

  if (typeof(result) === "function") {
    methodMap[url] = { fn: result };
  } else {
    methodMap[url] = { result };
  }
}

function __clearMocks() {
  REQUEST_MOCKS = {};
}

exports.request = mockRequest;
exports.__putMockResponse = __putMockResponse;
exports.__clearMocks = __clearMocks;
