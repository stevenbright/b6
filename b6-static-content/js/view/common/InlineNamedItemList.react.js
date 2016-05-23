'use strict';

import React, {Component} from 'react';

export default class InlineCatalogItemList extends Component<{}, {}, {}> {

  render(): ?ReactElement {
    if (this.props.items.length === 0) {
      return (<span className='text-muted'><i>none</i></span>);
    }

    const listElementsUi = this.props.items.map((item) => {
      const itemLink = "#/item/" + item.id;
      return (<li key={item.id}><a href={itemLink} title={item.title}>{item.title}</a></li>);
    });

    return (
      <ul className="csv">{listElementsUi}</ul>
    );
  }
}

