'use strict';

import React, {Component} from 'react';

export default class InlineCatalogItem extends Component<{}, {}, {}> {

  render(): ?ReactElement {
    const item = this.props.item;
    const itemHref = "#/item/" + item.id;

    return (
      <span className="inline-item">
        <a href={itemHref} title={this.props.item.title}>{this.props.item.title}</a>
      </span>
    );
  }
}
