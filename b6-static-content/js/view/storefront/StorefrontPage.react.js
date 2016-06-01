'use strict';

import React, {Component} from 'react';

import TitleService from '../../service/TitleService';
import CatalogService from '../../service/CatalogService';

import LoadingPage from '../common/LoadingPage.react';
import CatalogListItem from '../catalog/CatalogListItem.react';

export default class DetailPage extends Component<{},
  /*Props*/{ /*id: string*/ },
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
        <p><strong>Storefront</strong>&nbsp;Page</p>
        <div className="well">
          Lorem ipsum
        </div>
        <h3>Favorites</h3>
        <ul className="catalog-list">
          {itemsUi}
        </ul>
      </div>
    );
  }

  //
  // Private
  //

  _fetch(props): void {
    const p = CatalogService.getFavoriteItems({});
    p.then((response) => {
      this.setState({items: response["items"]});
    });
  }
}

