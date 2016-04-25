'use strict';

import React, {Component} from 'react';

type Props = {
  target: string
};

export default class LoadingPage extends Component<{}, Props, {}> {
  render(): ?ReactElement {
    const msg = "Loading " + this.props.target + "...";
    return (
      <div className="container">
        <p>{msg}</p>
      </div>
    );
  }
}
