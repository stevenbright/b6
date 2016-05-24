'use strict';

import React, {Component} from 'react';

import TitleService from '../../service/TitleService';
import CatalogService from '../../service/CatalogService';

import LoadingPage from '../common/LoadingPage.react';
import InlineNamedItemList from '../common/InlineNamedItemList.react';
import CatalogList from '../catalog/CatalogList.react';

import {ALL_TYPE_FILTER, MISSING_CURSOR, DEFAULT_SORT_TYPE} from '../../util/Constants';

export default class DetailPage extends Component<{},
  /*Props*/{ /*id: string*/ },
  /*State*/{}> {

  state = {
    item: null,
    relatedItems: null,
    relatedItemsCursor: null
  }

  componentDidMount(): void {
    this._fetch(this.props);
  }

  componentWillReceiveProps(nextProps): void {
    this._fetch(nextProps);
  }

  render(): ReactElement {
    if (this.state.item == null) {
      TitleService.setTitle("Loading Item Profile...");
      return (<LoadingPage target='Item'/>);
    }

    const item = this.state.item;
    TitleService.setTitle("Item \u00BB " + item.title);

    console.log('Item Page:', item, ', Related Items:', this.state.relatedItems);

    return (
      <div className="container">
        <h2>{item.title}</h2>
        <p><small>{item.id} | {item.type}</small></p>
        {this._renderItem(item)}
      </div>
    );
  }

  _renderItem(item): ReactElement {
    if (item.type === 'book') {
      return this._renderBook(item);
    }

    return this._renderNamed(item);
  }

  _renderBook(item): ReactElement {
    let downloadUi;
    if (item.book.downloadItems.length === 0) {
      downloadUi = (
        <div>
          <hr/>
          <small className="text-muted">No downloads for this item.</small>
        </div>
      );
    } else {
      const downloadItemsUi = item.book.downloadItems.map(downloadItem => (
        <li key={downloadItem.downloadUrl}>
          <a href={downloadItem.downloadUrl} target="_blank">{downloadItem.descriptorText}</a>&nbsp;
          <span>(File Size: {downloadItem.fileSize})</span>
        </li>
      ));

      downloadUi = (
        <div>
          <h3>Downloads</h3>
          <ul>
            {downloadItemsUi}
          </ul>
        </div>
      );
    }

    return (
      <div>
        <hr/>
        <table className="item-info">
          <tbody>
            <tr>
              <td>Authors:</td>
              <td><InlineNamedItemList items={item.book.authors}/></td>
            </tr>
            <tr>
              <td>Genres:</td>
              <td><InlineNamedItemList items={item.book.genres}/></td>
            </tr>
            <tr>
              <td>Language:</td>
              <td><InlineNamedItemList items={[item.book.language]}/></td>
            </tr>
            <tr>
              <td>Origins:</td>
              <td><InlineNamedItemList items={item.book.origins}/></td>
            </tr>
          </tbody>
        </table>
        {downloadUi}
      </div>
    );
  }

  _renderNamed(item): ReactElement {
    if (this.state.relatedItems == null) {
      return (
        <div>
          <hr/>
          <p>Loading related items...</p>
        </div>
      );
    }

    return (
      <div>
        <h3>Related Items</h3>
        <CatalogList items={this.state.relatedItems} cursor={this.state.relatedItemsCursor} createCatalogUrl={this._createCatalogUrl} />
      </div>
    );
  }

  _createCatalogUrl = (params) => {
    const req = {
      sortType: this.props.sortType,
      limit: this.props.limit,
      typeFilter: this.props.typeFilter,
      cursor: this.state.relatedItemsCursor
    }

    params = params || {};
    for (const key in params) {
      if (params.hasOwnProperty(key)) {
        req[key] = params[key];
      }
    }

    return '#/item/' + this.props.id + '/s/' + req.sortType + '/l/' + req.limit + '/t/' + req.typeFilter + '/c/' + req.cursor;
  }

  _fetch(props): void {
    let p;
    if (this.state.item == null || this.state.item.id != props.id) {
      console.log('before getItem');
      p = CatalogService.getItem(props.id);
      p.then(
        (response) => this.setState({ item: response['item'] }),
        (err) => console.log("Error:", err));
    } else {
      console.log('skipping getItem');
      p = new Promise((resolve, _) => {
        resolve({'item': this.state.item});
      });
    }

    p.then((response) => {
      const item = response['item'];

      // TODO: common code with CatalogPage._fetch
      const request = {
        //'nameFilter': 'T',
        'relatedItemId': item.id,
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

      const relatedItemPromise = CatalogService.getItems(request);
      //items: response['items'], cursor: response['cursor']
      relatedItemPromise.then((r) => this.setState({
        relatedItems: r['items'],
        relatedItemsCursor: r['cursor']
      }));
    });
  }
}

