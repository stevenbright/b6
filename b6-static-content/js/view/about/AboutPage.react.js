'use strict';

import React, {Component} from 'react';

export default class AboutPage extends Component<{}, {}, {}> {
  render(): ?ReactElement {
    return (
      <div className="container">
        <h2>B6 Admin Console</h2>
        <hr/>
        <p>&copy; Alex, 2016</p>
      </div>
    );
  }
}

