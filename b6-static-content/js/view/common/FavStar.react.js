'use strict';

import React, {Component} from 'react';

import CatalogService from '../../service/CatalogService';

type Props = {
  id: string,
  isFavorite: bool
};

type State = {
  isFavorite: bool,
  inProgress: bool
};

export default class FavStar extends Component<{}, Props, State> {
  state = {
    isFavorite: this.props.isFavorite,
    inProgress: false
  }

  render(): ?ReactElement {
    let favLinkClass = "j-fav-link";
    if (this.state.isFavorite) {
      favLinkClass += " fav";
    }

    if (this.state.inProgress) {
      favLinkClass += " text-muted";
      return (
        <span className={favLinkClass}>
          <span className="star"><span className="glyphicon glyphicon glyphicon-star" aria-hidden="true"></span>&nbsp;Unstar</span>
          <span className="unstar"><span className="glyphicon glyphicon glyphicon-star-empty" aria-hidden="true"></span>&nbsp;Star</span>
        </span>
      );
    }

    // TODO: use correct href
    return (
      <a className={favLinkClass} href="#" onClick={this._onClick}>
        <span className="star"><span className="glyphicon glyphicon glyphicon-star" aria-hidden="true"></span>&nbsp;Unstar</span>
        <span className="unstar"><span className="glyphicon glyphicon glyphicon-star-empty" aria-hidden="true"></span>&nbsp;Star</span>
      </a>
    );
  }

  _onClick = (event) => {
    event.preventDefault();
    const newFavStatus = !this.state.isFavorite;
    const isFavorite = newFavStatus;
    this.setState({
      isFavorite: isFavorite,
      inProgress: true
    });

    const p = CatalogService.setFavorite(this.props.id, isFavorite);
    p.then((response) => {
      this.setState({inProgress: false});
    });
  }
}
