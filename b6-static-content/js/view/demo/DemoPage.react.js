'use strict';

import React, {Component} from 'react';

// controls to be demo'ed:
import FavStar from '../common/FavStar.react';
import StarRating from '../common/StarRating.react';
import CatalogList from '../catalog/CatalogList.react';

// page to be demo'ed:
import LoadingPage from '../common/LoadingPage.react';

class DemoControlsPage extends Component<{}, {}, {}> {
  render(): ?ReactElement {
    const catalogListItems = [
      {id: 1, title: 'Sample Author', type: 'person', isFavorite: true},
      {id: 2, title: 'Sample Book N1', type: 'book', isFavorite: false},
      {
        id: 3,
        title: 'Another Sample Book',
        type: 'book',
        book: {
          authors: [
            {id: 1000, title: 'Jack London'}
          ],
          genres: [
            {id: 500, title: 'fiction'}
          ]
        },
        isFavorite: true
      }
    ];
    return (
      <div className="container">
        <div><span>Fav 1:&nbsp;</span><FavStar isFavorite={true}/></div>
        <div><span>Fav 2:&nbsp;</span><FavStar isFavorite={false}/></div>
        <hr />
        <div><span>Rating 0:&nbsp;</span><StarRating rating={0}/></div>
        <div><span>Rating 1:&nbsp;</span><StarRating rating={1}/></div>
        <div><span>Rating 2:&nbsp;</span><StarRating rating={2}/></div>
        <div><span>Rating 3:&nbsp;</span><StarRating rating={3}/></div>
        <div><span>Rating 4:&nbsp;</span><StarRating rating={4}/></div>
        <div><span>Rating 5:&nbsp;</span><StarRating rating={5}/></div>
        <hr />
        <h2>Catalog List</h2>
        <CatalogList items={catalogListItems} />
      </div>
    );
  }
}

//
// Page
//

export default class DemoPage extends Component<{}, {}, {}> {
  render(): ?ReactElement {
    let mode = "default";

    const modeEq = "mode=";
    const searchPart = window.location.search;
    const modeIndex = searchPart.indexOf(modeEq);
    if (modeIndex >= 0) {
      mode = searchPart.substring(modeIndex + modeEq.length);
    }

    if (mode == "loading") {
      return <LoadingPage target={"demo"} />;
    } else if (mode == "eolaireApi") {
      return <EolaireApiConsoleView />;
    } else if (mode == "demoControls") {
      return <DemoControlsPage />;
    }

    return (
      <div className="container">
        <ul>
          <li><a href="/?mode=loading#/demo">Loading Page</a></li>
          <li><a href="/?mode=demoControls#/demo">Demo Controls</a></li>
        </ul>
      </div>
    );
  }
}

