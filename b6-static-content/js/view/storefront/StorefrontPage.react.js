'use strict';

import React, {Component} from 'react';

import {MISSING_CURSOR} from '../../util/Constants';

import TitleService from '../../service/TitleService';
import CatalogService from '../../service/CatalogService';

import LoadingPage from '../common/LoadingPage.react';
import CatalogListItem from '../catalog/CatalogListItem.react';

export default class DetailPage extends Component<{},
  /*Props*/{
    /*limit: number, cursor: string*/
  },
  /*State*/{}> {

  state = {
    items: null,
    cursor: null
  }

  componentDidMount(): void {
    this._fetch(this.props);
  }

  componentWillReceiveProps(nextProps): void {
    this._fetch(nextProps);
  }

  render(): ReactElement {
    if (this.state.items === null) {
      TitleService.setTitle("Loading Storefront...");
      return (<LoadingPage target='Storefront'/>);
    }

    TitleService.setTitle("Storefront");

    const itemsUi = this.state.items.map(function (item) {
      return (<CatalogListItem key={item.id} item={item}/>);
    });

    return (
      <div className="container">
        <h3>Favorites</h3>
        <ul className="catalog-list">
          {itemsUi}
        </ul>
        <hr/>
        {this._getPaginationLink()}
      </div>
    );
  }

  //
  // Private
  //

  _getPaginationLink() {
    if (this.state.cursor != null) {
      const link = '#/storefront/l/' + this.props.limit + '/c/' + this.state.cursor;
      return (
        <div>
          <a href={link} role="button" className="btn btn-info">Next</a>
        </div>
      );
    } else {
      return [];
    }
  }

  _fetch(props): void {
    const request = {
      'limit': props.limit
    };

    if (props.cursor != MISSING_CURSOR) {
      request['cursor'] = props.cursor;
    }

    const p = CatalogService.getFavoriteItems(request);
    p.then((response) => {
      this.setState({items: response["items"], cursor: response["cursor"]});
    });
  }
}

