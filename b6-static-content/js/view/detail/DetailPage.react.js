'use strict';

import React, {Component} from 'react';

import LoadingPage from '../common/LoadingPage.react';

import CatalogService from '../../service/CatalogService';

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

    let relatedItems = [];
    let itemProfile = [];
    if (item.type === 'book') {
      relatedItems = (<p>QQQ!</p>);
      itemProfile = (
        <div>
          <p>TODO: nested</p>
        </div>
      );
    } else {
      relatedItems = (<p>TODO: Loading related items ...</p>);
    }

    return (
      <div className="container">
        <h2>{item.title}</h2>
        <p><small>{item.id} | {item.type}</small></p>
        <hr/>
        {relatedItems}
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

