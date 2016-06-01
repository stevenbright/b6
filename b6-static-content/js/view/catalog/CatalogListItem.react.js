'use strict';

import React, {Component} from 'react';

import FavStar from '../common/FavStar.react';
import InlineNamedItemList from '../common/InlineNamedItemList.react';

export default class CatalogListItem extends Component<{},
  /*Props*/{ item: object },
  /*State*/{}> {

  render(): ?ReactElement {
    const item = this.props.item;
    const itemDetailPageUrl = '#/item/' + item.id;
    const isFavorite = item.isFavorite;

    // related items UI
    let profileUi = [];
    if ("book" in item) {
      const profile = item.book;
      profileUi = (
        <span className="related-items">
          <span className="glyphicon glyphicon-book" ariaHidden="true"></span>
          <span className="related-items-header">Authors:&nbsp;</span><InlineNamedItemList items={profile.authors}/>
          <span className="related-items-header">Genres:&nbsp;</span><InlineNamedItemList items={profile.genres}/>
        </span>
      );
    }

    const hrefTitle = item.id + ' | ' + item.title;

    return (
      <li>
        <div className="container">
          <div className="row">
            <div className="col-md-12">
              <p className="pull-right"><span className="label label-default">{item.type}</span></p>
              <h4 className="pull-left"><a href={itemDetailPageUrl} title={hrefTitle}>{item.title}</a></h4>
            </div>
          </div>
          <div className="row">
            <div className="col-md-2">
              <FavStar id={item.id} isFavorite={isFavorite}/>
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
