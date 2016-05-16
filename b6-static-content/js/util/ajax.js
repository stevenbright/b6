
import HttpRequest from './xhr.js';

let GLOBAL_ERROR_HANDLER = function defaultGlobalErrorHandler(xmlHttpRequest) {
  console.error("AJAX error, status:", xmlHttpRequest.status, xmlHttpRequest.statusText,
      "responseURL:", xmlHttpRequest.responseURL);
}

/**
 * Executes global error handlers. Sample error handler is shown below:
 * <code>
 * function sampleErrorHandler(xmlHttpRequest) {
 *   window["lastErrorXhr"] = xmlHttpRequest;
 *   console.error("AJAX error, status:", xmlHttpRequest.status, xmlHttpRequest.statusText,
 *       "responseURL:", xmlHttpRequest.responseURL);
 * }
 * </code>
 *
 * Installation of an error handler is just a subscription on XHR_ERROR event:
 * <code>
 * var ajax = rsvp('rsvp-ajax');
 * ...
 * ajax.on(ajax.XHR_ERROR, sampleErrorHandler);
 * </code>
 */
function onError(xmlHttpRequest) {
  console.error("AJAX error, status:", xmlHttpRequest.status, xmlHttpRequest.statusText,
    "responseURL:", xmlHttpRequest.responseURL);
}

/** Helper function, that creates a handler for XMLHttpRequest.onreadystatechange */
function createHttpRequestHandler(resolve, reject) {
  return function xmlHttpRequestHandler() {
    if (this.readyState !== this.DONE) {
      return;
    }

    if (this.status === 200 || this.status === 201 || this.status === 204) {
      resolve(this.response);
    } else {
      onError(this);
      reject(this);
    }
  };
}

/**
 * Creates a new HTTP request for data fetched by using async AJAX interface.
 *
 * @arg options Request options:
 *    <tt>options.method</tt> String, that identifies HTTP request method, e.g. 'GET', 'PUT', 'POST', 'DELETE'
 *    <tt>url</tt> URL to the AJAX resource, e.g. '/rest/ajax/foo/bar/baz'
 *    <tt>requestBody</tt> An object, that represents a request, can be null
 *    <tt>responseType</tt> Response type code, can be null - if so, default value 'text' will be picked up
 *    <tt>accept</tt> MIME type to be passed in 'Accept' header
 *    <tt>contentType</tt> MIME type to be put to 'Content-Type' header, can be null if requestBody is null
 *
 * @return A new Promise instance
 */
function requestObject(options) {
  const responseType = options.responseType || "text";
  const method = options.method || "GET";
  const url = options.url || "/";
  const requestBody = options.requestBody || null;
  const accept = options.accept || "*/*";
  const contentType = options.contentType || null;

  return new Promise(function(resolve, reject) {
    const client = new HttpRequest();
    client.open(method, url);
    client.onreadystatechange = createHttpRequestHandler(resolve, reject);
    client.responseType = responseType;
    client.setRequestHeader("Accept", accept);

    if (requestBody != null) {
      client.setRequestHeader("Content-Type", contentType);
      client.send(requestBody);
    } else {
      client.send();
    }
  });
}

/**
 * Creates a new HTTP request for data fetched by using async AJAX interface.
 *
 * @arg method String, that identifies HTTP request method, e.g. 'GET', 'PUT', 'POST', 'DELETE'
 * @arg url URL to the AJAX resource, e.g. '/rest/ajax/foo/bar/baz'
 * @arg requestBody An object, that represents a request, can be null
 * @return A new rsvp.Promise instance
 */
function request(method, url, requestBody) {
  return requestObject({
    method,
    url,
    requestBody: (requestBody != null ? JSON.stringify(requestBody) : null),
    accept: "application/json",
    contentType: "application/json",
    responseType: "json"
  });
}

//
// Exports
//

// Event Names
module.exports.GLOBAL_ERROR_HANDLER = GLOBAL_ERROR_HANDLER;

// Making AJAX requests
module.exports.requestObject = requestObject;
module.exports.request = request;
