
'use strict';

import React, {Component} from 'react';
import {Router} from 'director';

// widgets

import StorefrontPage from './storefront/StorefrontPage.react';
import CatalogPage from './catalog/CatalogPage.react';
import AboutPage from './about/AboutPage.react';
import DetailPage from './detail/DetailPage.react';
import DemoPage from './demo/DemoPage.react';

import TitleService from '../service/TitleService';

// navigation IDs

const Nav = {
  UNDEFINED:    "UNDEFINED",
  STOREFRONT:   "STOREFRONT",
  CATALOG:      "CATALOG",
  DETAIL:       "DETAIL",
  DEMO:         "DEMO",
  ABOUT:        "ABOUT"
};

export default class ViewDispatcher extends Component<{}, {}, /*State*/{}> {
  state = {
    nowShowing: Nav.UNDEFINED,

    id: undefined,
    cursor: undefined,
    limit: undefined
  }

  componentDidMount(): void {
    const gotoStorefrontPage = this.setState.bind(this, {nowShowing: Nav.STOREFRONT});

    const gotoCatalogPage = this.setState.bind(this, {nowShowing: Nav.CATALOG, cursor: null, limit: 8});
    const gotoCatalogPageCursorLimit = (cursor, limit) => this.setState({
      nowShowing: Nav.CATALOG,
      cursor,
      limit: parseInt(limit)
    });

    const gotoAboutPage = this.setState.bind(this, {nowShowing: Nav.ABOUT});
    const gotoDetailPage = (id) => this.setState({nowShowing: Nav.DETAIL, id: id});

    // TODO: disable in prod
    const gotoDemoPage = this.setState.bind(this, {nowShowing: Nav.DEMO});

    const router = Router({
      '/storefront': gotoStorefrontPage,

      '/catalog/:cursor/page/:limit': gotoCatalogPageCursorLimit,
      '/catalog': gotoCatalogPage,

      '/demo': gotoDemoPage,
      '/item/:id': gotoDetailPage,
      '/about': gotoAboutPage
    });

    router.init('/storefront');
  }

  render(): ?ReactElement {
    console.log("state =", this.state.nowShowing);

    switch (this.state.nowShowing) {
      case Nav.STOREFRONT:
        TitleService.setTitle("Storefront");
        return (<StorefrontPage />);

      case Nav.CATALOG:
        TitleService.setTitle("Catalog");
        return (<CatalogPage cursor={this.state.cursor} limit={this.state.limit} />);

      case Nav.DETAIL:
        TitleService.setTitle("Details");
        return (<DetailPage id={this.state.id} />);

      case Nav.ABOUT:
        TitleService.setTitle("About");
        return (<AboutPage />);

      case Nav.DEMO: // should be inactive in prod
        TitleService.setTitle("Demo");
        return (<DemoPage />);

      default:
        TitleService.setTitle("Loading...");
        return (<div>Looking for something?...</div>);
    }
  }
}

