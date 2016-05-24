'use strict';

import React, {Component} from 'react';
import CatalogListItem from './CatalogListItem.react';

import {ALL_TYPE_FILTER, MISSING_CURSOR, DEFAULT_SORT_TYPE} from '../../util/Constants';

export default class CatalogList extends Component<{},
  /*Props*/{ /* items, cursor */ },
  /*State*/{}> {

  render(): ?ReactElement {
    const itemsUi = this.props.items.map(function (item) {
      return (<CatalogListItem key={item.id} item={item}/>);
    });



    return (
      <div className="container">
        {this._getCatalogQueryUi()}
        <br/>
        <ul className="catalog-list">
          {itemsUi}
        </ul>
        <hr/>
        {this._getPaginationLink()}
      </div>
    );
  }

  _getCatalogQueryUi() {
    return (
      <div className="btn-toolbar" role="toolbar">
        <div className="btn-group btn-group-xs" role="group">
          <a href={this._getTypeFilterUrl(ALL_TYPE_FILTER)} role="button" className="btn btn-default">All</a>
          <a href={this._getTypeFilterUrl('book')} role="button" className="btn btn-default">Book</a>
          <a href={this._getTypeFilterUrl('person')} role="button" className="btn btn-default">Person</a>
          <a href={this._getTypeFilterUrl('genre')} role="button" className="btn btn-default">Genre</a>
          <a href={this._getTypeFilterUrl('origin')} role="button" className="btn btn-default">Origin</a>
          <a href={this._getTypeFilterUrl('language')} role="button" className="btn btn-default">Language</a>
        </div>
        <div className="btn-group btn-group-xs" role="group">
          <a href={this._getSortTypeUrl(DEFAULT_SORT_TYPE)} role="button" className="btn btn-default">No Sort</a>
          <a href={this._getSortTypeUrl('TITLE_ASCENDING')} role="button" className="btn btn-default">Sort: A-Z</a>
          <a href={this._getSortTypeUrl('TITLE_DESCENDING')} role="button" className="btn btn-default">Sort: Z-A</a>
        </div>
      </div>
    );
  }

  _getPaginationLink() {
    if (this.props.cursor != null) {
      const link = this.props.createCatalogUrl();
      return (
        <div>
          <a href={link} role="button" className="btn btn-info">Next</a>
        </div>
      );
    } else {
      return [];
    }
  }

  _getTypeFilterUrl(typeFilter): string {
    return this.props.createCatalogUrl({typeFilter, cursor: MISSING_CURSOR});
  }

  _getSortTypeUrl(sortType): string {
    return this.props.createCatalogUrl({sortType, cursor: MISSING_CURSOR});
  }
}
