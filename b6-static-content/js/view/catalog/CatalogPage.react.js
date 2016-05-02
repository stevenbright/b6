'use strict';

import React, {Component} from 'react';

import CatalogList from './CatalogList.react';

export default class CatalogPage extends Component<{}, {}, {}> {
  render(): ?ReactElement {
    const catalogListItems = [
      {id: 1, title: 'Sample Author', type: 'person', isFavorite: true},
      {id: 2, title: 'Sample Book N1', type: 'book', isFavorite: false},
      {
        id: 3,
        title: 'Another Sample Book',
        type: 'book',
        book: {
          authors: [
            {id: 1000, title: 'Jack London'}
          ],
          genres: [
            {id: 500, title: 'fiction'}
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
}

