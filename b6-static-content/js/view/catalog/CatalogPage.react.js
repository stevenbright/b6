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

        <div className="btn-toolbar" role="toolbar">
          <div className="btn-group btn-group-xs" role="group">
            <button type="button" className="btn btn-default">None</button>
            <button type="button" className="btn btn-default">Book</button>
            <button type="button" className="btn btn-default">Person</button>
            <button type="button" className="btn btn-default">Genre</button>
            <button type="button" className="btn btn-default">Origin</button>
            <button type="button" className="btn btn-default">Language</button>
          </div>
          <div className="btn-group btn-group-xs" role="group">
            <button type="button" className="btn btn-default">Default</button>
            <button type="button" className="btn btn-default">Title: A-Z</button>
            <button type="button" className="btn btn-default">Title: Z-A</button>
          </div>
        </div>
        <br/>

        <CatalogList items={this.state.items} />
        <hr/>
        {paginationLink}
      </div>
    );
  }

  _fetch(props): void {
    const p = CatalogService.getItems(props.cursor, props.limit);
    p.then(
      (response) => this.setState({ items: response['items'], cursor: response['cursor'], loading: false }),
      (err) => console.log("Error:", err));
  }
}

