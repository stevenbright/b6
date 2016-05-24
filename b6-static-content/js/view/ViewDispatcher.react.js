
'use strict';

import React, {Component} from 'react';
import {Router} from 'director';
import {DEFAULT_LIMIT, ALL_TYPE_FILTER, MISSING_CURSOR, ALL_NAME_FILTER, DEFAULT_SORT_TYPE} from '../util/Constants';

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
    typeFilter: ALL_TYPE_FILTER,
    cursor: MISSING_CURSOR,
    nameFilter: ALL_NAME_FILTER,
    sortType: DEFAULT_SORT_TYPE,
    limit: DEFAULT_LIMIT
  }

  componentDidMount(): void {
    const gotoStorefrontPage = this.setState.bind(this, {nowShowing: Nav.STOREFRONT});

    const gotoCatalogPage = this.setState.bind(this, {
      nowShowing: Nav.CATALOG,
      typeFilter: ALL_TYPE_FILTER,
      cursor: MISSING_CURSOR,
      sortType: DEFAULT_SORT_TYPE,
      limit: DEFAULT_LIMIT
    });
    const gotoCatalogPageWithParams = (sortType, limit, typeFilter, cursor) => this.setState({
      nowShowing: Nav.CATALOG,
      typeFilter,
      cursor,
      sortType,
      limit: parseInt(limit)
    });

    const gotoDetailPageWithParams = (id, sortType, limit, typeFilter, cursor) => this.setState({
      nowShowing: Nav.DETAIL,
      id,
      typeFilter,
      cursor,
      sortType,
      limit: parseInt(limit)
    });
    const gotoDetailPage = (id) => this.setState({
      nowShowing: Nav.DETAIL,
      id,
      typeFilter: ALL_TYPE_FILTER,
      cursor: MISSING_CURSOR,
      sortType: DEFAULT_SORT_TYPE,
      limit: DEFAULT_LIMIT
    });

    const gotoAboutPage = this.setState.bind(this, {nowShowing: Nav.ABOUT});

    // TODO: disable in prod
    const gotoDemoPage = this.setState.bind(this, {nowShowing: Nav.DEMO});

    const router = Router({
      '/storefront': gotoStorefrontPage,

      '/catalog/s/:sort/l/:limit/t/:type/c/:cursor': gotoCatalogPageWithParams,
      '/catalog': gotoCatalogPage,

      '/item/:id/s/:sort/l/:limit/t/:type/c/:cursor': gotoDetailPageWithParams,
      '/item/:id': gotoDetailPage,


      '/demo': gotoDemoPage,
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
        return (
          <CatalogPage
            typeFilter={this.state.typeFilter}
            sortType={this.state.sortType}
            cursor={this.state.cursor}
            limit={this.state.limit}
            />
        );

      case Nav.DETAIL:
        return (
          <DetailPage
            id={this.state.id}
            typeFilter={this.state.typeFilter}
            sortType={this.state.sortType}
            cursor={this.state.cursor}
            limit={this.state.limit}
            />
        );

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

