'use strict';

import React, {Component} from 'react';

import TitleService from '../../service/TitleService';
import CatalogService from '../../service/CatalogService';

import LoadingPage from '../common/LoadingPage.react';
import InlineNamedItemList from '../common/InlineNamedItemList.react';

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

  render(): ReactElement {
    if (this.state.loading) {
      TitleService.setTitle("Loading Item Profile...");
      return (<LoadingPage target='Item'/>);
    }

    const item = this.state.item;
    TitleService.setTitle("Item \u00BB " + item.title);

    console.log('Item Page', item);

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
          <p className="text-muted">No Downloads</p>
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
    return (
      <div>
        <hr/>
        <p>TODO: Loading related items ...</p>
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

