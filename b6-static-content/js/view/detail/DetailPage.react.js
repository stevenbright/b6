'use strict';

import React, {Component} from 'react';

import LoadingPage from '../common/LoadingPage.react';
import CatalogService from '../../service/CatalogService';
import InlineCatalogItem from '../catalog/InlineCatalogItem.react';

export default class DetailPage extends Component<{},
  /*Props*/{ /*id: string*/ },
  /*State*/{}> {

  state = {
    loading: true,
    loadingRelatedItems: true,
    item: null
  }

  componentDidMount(): void {
    this._fetch(this.props);
  }

  componentWillReceiveProps(nextProps): void {
    this._fetch(nextProps);
  }

  render(): ?ReactElement {
    if (this.state.loading) {
      return (<LoadingPage target='Item'/>);
    }

    const item = this.state.item;

    console.log('DP item', item);

    let itemProfile = [];
    if (item.type === 'book') {
      const authorsUi = item.book.authors.map((author) => (<InlineCatalogItem key={author.id} item={author} />));
      const genresUi = item.book.genres.map((genre) => (<InlineCatalogItem key={genre.id} item={genre} />));

      itemProfile = (
        <div>
          <hr/>
          <table className="item-info">
            <tbody>
              <tr>
                <td>Authors:</td>
                <td>{authorsUi}</td>
              </tr>
              <tr>
                <td>Genres:</td>
                <td>{genresUi}</td>
              </tr>
            </tbody>
          </table>
        </div>
      );
    } else {
      itemProfile = (
        <div>
          <hr/>
          <p>TODO: Loading related items ...</p>
        </div>
      );
    }

    return (
      <div className="container">
        <h2>{item.title}</h2>
        <p><small>{item.id} | {item.type}</small></p>
        {itemProfile}
      </div>
    );
  }

  _fetch(props): void {
    const p = CatalogService.getItem(props.id);
    p.then(
      (response) => this.setState({ item: response['item'], loading: false }),
      (err) => console.log("Error:", err));
  }
}

