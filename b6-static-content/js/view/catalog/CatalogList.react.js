'use strict';

import React, {Component} from 'react';
import CatalogListItem from './CatalogListItem.react';

export default class CatalogList extends Component<{}, /*Props*/{}, /*State*/{}> {

  render(): ?ReactElement {
    const itemsUi = this.props.items.map(function (item) {
      return (<CatalogListItem key={item.id} item={item}/>);
    });

    // TODO: pagination
    return (
      <div className="container">
        <ul className="catalog-list">
          {itemsUi}
        </ul>
      </div>
    );
  }
}
