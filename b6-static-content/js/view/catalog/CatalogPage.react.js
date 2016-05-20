'use strict';

import React, {Component} from 'react';

import CatalogList from './CatalogList.react';
import LoadingPage from '../common/LoadingPage.react';

import CatalogService from '../../service/CatalogService';

export default class CatalogPage extends Component<{},
  /*Props*/{ /*cursor: string, limit: number, itemType: string*/ },
  /*State*/{}> {

  state = {
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
    if (this.state.loading) {
      return (<LoadingPage target='Catalog'/>);
    }

    console.log('items', this.state.items, 'cursor', this.state.cursor);

    let paginationLink = [];
    if (this.state.cursor != null) {
      const link = '#/catalog/' + this.state.cursor + '/page/' + this.props.limit;
      paginationLink = (
        <div>
          <a href={link}>Next</a>
        </div>
      );
    }

    return (
      <div className="container">
        <h2>Catalog</h2>
        <CatalogList items={this.state.items} />
        <hr/>
        {paginationLink}
      </div>
    );
  }

  _fetch(props): void {
    console.log("About to fetch catalog items", props);

    const p = CatalogService.getItems(props.cursor, props.limit);
    p.then(
      (response) => this.setState({ items: response['items'], cursor: response['cursor'], loading: false }),
      (err) => console.log("Error:", err));
  }
}

