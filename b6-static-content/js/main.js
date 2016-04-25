import React from 'react';
import ReactDOM from 'react-dom';

import ViewDispatcher from './view/ViewDispatcher.react';

import {parseQueryString} from './util/uri';
import cache from 'rsvp-cache';
import ajax from 'rsvp-ajax';


function installDebugHooks() {
  console.log("Installing Debug Hooks...");

  // install AJAX interceptor
  ajax.on(ajax.XHR_ERROR, function (xmlHttpRequest) {
    window["lastErrorXhr"] = xmlHttpRequest;
    console.error("AJAX error; status:", xmlHttpRequest.status, xmlHttpRequest.statusText,
      "responseURL:", xmlHttpRequest.responseURL, "requestId:", xmlHttpRequest.getResponseHeader("X-Rid"));
  });

  // install cache event handlers
  cache.on(cache.CACHE_HIT, function (d) {
    console.log("cacheHit, key:", d.key, ", value:", d.value);
  });

  cache.on(cache.CACHE_MISS, function (d) {
    console.log("cacheMiss, key:", d.key);
  });
}

window.onload = function () {
  const queryParam = parseQueryString(window.location.search);
  if (queryParam["debug"] === "1") {
    installDebugHooks();
  }

  ReactDOM.render(React.createElement(ViewDispatcher), document.getElementById('main-content'));
}
