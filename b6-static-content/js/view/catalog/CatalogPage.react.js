'use strict';

import React, {Component} from 'react';

import {ALL_TYPE_FILTER, MISSING_CURSOR, DEFAULT_SORT_TYPE} from '../../util/Constants';

import CatalogList from './CatalogList.react';
import LoadingPage from '../common/LoadingPage.react';

import CatalogService from '../../service/CatalogService';

export default class CatalogPage extends Component<{},
  /*Props*/{
    /*limit: number, typeFilter: string, nameFilter: string, cursor: string*/
  },
  /*State*/{}> {

  state = {
    items: null
  }

  componentDidMount(): void {
    this._fetch(this.props);
  }

  componentWillReceiveProps(nextProps): void {
    this._fetch(nextProps);
  }

  render(): ?ReactElement {
    if (this.state.items == null) {
      return (<LoadingPage target='Catalog'/>);
    }

    return (
      <div className="container">
        <h2>Catalog</h2>
        <CatalogList items={this.state.items} cursor={this.state.cursor} createCatalogUrl={this._createCatalogUrl} />
      </div>
    );
  }

  _createCatalogUrl = (params) => {
    const req = {
      sortType: this.props.sortType,
      limit: this.props.limit,
      typeFilter: this.props.typeFilter,
      cursor: this.state.cursor
    }

    params = params || {};
    for (const key in params) {
      if (params.hasOwnProperty(key)) {
        req[key] = params[key];
      }
    }

    return '#/catalog/s/' + req.sortType + '/l/' + req.limit + '/t/' + req.typeFilter + '/c/' + req.cursor;
  }

  _fetch(props): void {
    // TODO: common code with DetailPage._fetch
    const request = {
      //'nameFilter': 'T',
      'limit': props.limit
    };

    if (props.cursor != MISSING_CURSOR) {
      request['cursor'] = props.cursor;
    }

    if (props.typeFilter != ALL_TYPE_FILTER) {
      request['typeFilter'] = props.typeFilter;
    }

    if (props.sortType != DEFAULT_SORT_TYPE) {
      request['sortType'] = props.sortType;
    }

    const p = CatalogService.getItems(request);
    p.then(
      (response) => this.setState({ items: response['items'], cursor: response['cursor'] }),
      (err) => console.log("Error:", err));
  }
}
