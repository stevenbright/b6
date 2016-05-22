'use strict';

import React, {Component} from 'react';

import {ALL_TYPE_FILTER, MISSING_CURSOR, DEFAULT_SORT_TYPE} from '../../util/Constants';

import CatalogList from './CatalogList.react';
import LoadingPage from '../common/LoadingPage.react';

import CatalogService from '../../service/CatalogService';

function createCatalogUrl(sortType, limit, typeFilter, cursor) {
  return '#/catalog/s/' + sortType + '/l/' + limit + '/t/' + typeFilter + '/c/' + cursor;
}

export default class CatalogPage extends Component<{},
  /*Props*/{
    /*limit: number, typeFilter: string, nameFilter: string, cursor: string*/
  },
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
      const link = createCatalogUrl(this.props.sortType, this.props.limit, this.props.typeFilter, this.state.cursor);
      paginationLink = (
        <div>
          <a href={link}>Next</a>
        </div>
      );
    }

    const getTypeFilterUrl = (typeFilter) => {
      return createCatalogUrl(this.props.sortType, this.props.limit, typeFilter, MISSING_CURSOR);
    }

    const getSortTypeUrl = (sortType) => {
      return createCatalogUrl(sortType, this.props.limit, this.props.typeFilter, MISSING_CURSOR);
    }

    return (
      <div className="container">
        <h2>Catalog</h2>

        <div className="btn-toolbar" role="toolbar">
          <div className="btn-group btn-group-xs" role="group">
            <a href={getTypeFilterUrl(ALL_TYPE_FILTER)} role="button" className="btn btn-default">All</a>
            <a href={getTypeFilterUrl('book')} role="button" className="btn btn-default">Book</a>
            <a href={getTypeFilterUrl('person')} role="button" className="btn btn-default">Person</a>
            <a href={getTypeFilterUrl('genre')} role="button" className="btn btn-default">Genre</a>
            <a href={getTypeFilterUrl('origin')} role="button" className="btn btn-default">Origin</a>
            <a href={getTypeFilterUrl('language')} role="button" className="btn btn-default">Language</a>
          </div>
          <div className="btn-group btn-group-xs" role="group">
            <a href={getSortTypeUrl(DEFAULT_SORT_TYPE)} role="button" className="btn btn-default">No Sort</a>
            <a href={getSortTypeUrl('TITLE_ASCENDING')} role="button" className="btn btn-default">Sort: A-Z</a>
            <a href={getSortTypeUrl('TITLE_DESCENDING')} role="button" className="btn btn-default">Sort: Z-A</a>
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
      (response) => this.setState({ items: response['items'], cursor: response['cursor'], loading: false }),
      (err) => console.log("Error:", err));
  }
}
