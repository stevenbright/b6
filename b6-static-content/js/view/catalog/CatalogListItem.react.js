'use strict';

import React, {Component} from 'react';

import FavStar from '../common/FavStar.react';
import InlineCatalogItem from './InlineCatalogItem.react';

export default class CatalogListItem extends Component<{},
  /*Props*/{ item: object },
  /*State*/{}> {

  render(): ?ReactElement {
    // {id: 1, title: 'Item Name', type: 'book', book: { authors: [{id: 2, name: 'as'}] }}
    const item = this.props.item;
    const itemDetailPageUrl = '#/item/' + item.id;
    const isFavorite = item.isFavorite;

    // related items UI
    let profileUi = [];
    if ("book" in item) {
      const profile = item.book;
      const authorsUi = profile.authors.map((author) => (<InlineCatalogItem key={author.id} item={author} />));
      const genresUi = profile.genres.map((genre) => (<InlineCatalogItem key={genre.id} item={genre} />));

      profileUi = (
        <span className="related-items">
          <span className="glyphicon glyphicon-book" ariaHidden="true"></span>
          <span className="related-items-header">Authors:&nbsp;</span>{authorsUi}
          <span className="related-items-header">Genres:&nbsp;</span>{genresUi}
        </span>
      );
    }

    return (
      <li>
        <div className="container">
          <div className="row">
            <div className="col-md-12">
              <p className="pull-right"><span className="label label-default">{item.type}</span><small className="text-muted">&nbsp;|&nbsp;{item.id}</small></p>
              <h4 className="pull-left"><a href={itemDetailPageUrl} title={item.title}>{item.title}</a></h4>
            </div>
          </div>
          <div className="row">
            <div className="col-md-2">
              <FavStar id={item.id} type={item.type} isFavorite={isFavorite}/>
            </div>
            <div className="col-md-10">
              {profileUi}
            </div>
          </div>
        </div>
      </li>
    );
  }
}
