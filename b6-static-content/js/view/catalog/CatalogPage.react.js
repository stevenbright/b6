'use strict';

import React, {Component} from 'react';

import CatalogList from './CatalogList.react';

export default class CatalogPage extends Component<{},
  /*Props*/{ /*cursor: string, limit: number, itemType: string*/ },
  /*State*/{}> {
  state: {
    loading: true,
    items: []
  }

  componentDidMount(): void {
    this._fetch(this.props);
  }

  componentWillReceiveProps(nextProps): void {
    this._fetch(nextProps);
  }

  render(): ?ReactElement {

    const catalogListItems = [
      {id: "A1", title: 'Sample Author', type: 'person', isFavorite: true},
      {id: "A2", title: 'Sample Book N1', type: 'book', isFavorite: false},
      {
        id: "A3",
        title: 'Another Sample Book',
        type: 'book',
        book: {
          authors: [
            {id: "A1000", title: 'Jack London'}
          ],
          genres: [
            {id: "A500", title: 'fiction'}
          ]
        },
        isFavorite: true
      }
    ];

    return (
      <div className="container">
        <h2>Catalog</h2>
        <CatalogList items={catalogListItems} />
      </div>
    );
  }

  _fetch(props): void {
    console.log("About to fetch catalog items", props);
    const itemId = props.itemId;
    const itemType = props.itemType;

    const p = CatalogAdapterService.getItem(itemId);
    p.then(
      (response) => this.setState({ itemDetails: response, loading: false }),
      (err) => console.log("Error:", err));
  }
}

