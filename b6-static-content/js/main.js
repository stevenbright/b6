import React from 'react';
import ReactDOM from 'react-dom';

import ViewDispatcher from './view/ViewDispatcher.react';

window.onload = function () {
  ReactDOM.render(React.createElement(ViewDispatcher), document.getElementById('main-content'));
}
