'use strict';

import React, {Component} from 'react';

// controls to be demo'ed:
import FavStar from '../common/FavStar.react';
import StarRating from '../common/StarRating.react';

// page to be demo'ed:
import LoadingPage from '../common/LoadingPage.react';

// parsing query
import {parseQueryString} from '../../util/uri.js';

class DemoControlsPage extends Component<{}, {}, {}> {
  render(): ?ReactElement {
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
      </div>
    );
  }
}

//
// Page
//

export default class DemoPage extends Component<{}, {}, {}> {
  render(): ?ReactElement {
    const queryParam = parseQueryString(window.location.search);
    const mode = ("mode" in queryParam ? queryParam["mode"] : "default");

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

