'use strict';

import React, {Component} from 'react';

export default class DetailPage extends Component<{}, {}, {}> {

  render(): ?ReactElement {
    const id = this.props.id;

    return (
      <div className="container">
        <h2>Detail Page</h2>
        <hr/>
        <p>TODO: replace with real page</p>
        <p>ID={id}</p>
      </div>
    );
  }
}

