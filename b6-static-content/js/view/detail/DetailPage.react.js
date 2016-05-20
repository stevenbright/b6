'use strict';

import React, {Component} from 'react';

import LoadingPage from '../common/LoadingPage.react';

import CatalogService from '../../service/CatalogService';

export default class DetailPage extends Component<{},
  /*Props*/{ /*id: string*/ },
  /*State*/{}> {

  state = {
    loading: true,
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

    return (
      <div className="container">
        <h2>{item.title}</h2>
        <p><small>{item.id} | {item.type}</small></p>
        <hr/>
        <p>Loading related items...</p>
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

