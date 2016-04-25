'use strict';

import React, {Component} from 'react';

//
// Helper functions
//

function getRatingFromDOMEvent(e, props) {
  const star = e.target;
  const allStars = Array.prototype.slice.call(e.currentTarget.childNodes, 0);
  const index = allStars.indexOf(star);

  let rating = props.total - index;
  let limit = Number(props.limit);

  limit = (props.limit === void 0) ? props.total: limit
  rating = rating < limit? rating: limit;

  return Number(rating);
}

//
// Helper subcomponent
//

// See also http://babeljs.io/blog/2015/06/07/react-on-es6-plus/

class Star extends Component<{}, {}, {}> {
  static defaultProps = {
    isActive: false,
    isDisabled: false
  }

  static propTypes = {
    isActive: React.PropTypes.bool.isRequired,
    isDisabled: React.PropTypes.bool.isRequired
  }

  render(): ?ReactElement {
    let className = this.props.isActive? 'is-active': '';
    className += this.props.isDisabled? ' is-disabled': '';

    return <a className={className}>&#9733;</a>;
  }
}

//
// StarRating component
//

export default class StarRating extends Component<{}, {}, {}> {

  state = {
    lastRating: this.props.rating,
    rating: this.props.rating
  }

  static defaultProps = {
    total: 5,
    rating: 0
  }

  componentDidMount() {
    this.setState({ rating: this.props.rating });
  }

  render(): ?ReactElement {
    let total = Number(this.props.total);
    let limit = Number(this.props.limit);
    const rating = Number(this.state.rating);

    limit = (this.props.limit === void 0) ? total : limit;

    const nodes = Array(total).join(',').split(',').map(function (_, i) {
      const isActive = (i >= total - rating);
      const isDisabled = (i < total - limit);
      return <Star key={i} isActive={isActive} isDisabled={isDisabled} />;
    }.bind(this));

    return (
      <span
        className="star-rating"
        onMouseEnter={this._handleMouseEnter}
        onMouseLeave={this._handleMouseLeave}
        onMouseMove={this._handleMouseMove}
        onClick={this._handleClick}>{nodes}
      </span>
    );
  }

  //
  // StarRating Private
  //

  _handleMouseEnter = () => {
    this.setState({ rating: 0 });
  }

  _handleMouseMove = (e) => {
    const rating = getRatingFromDOMEvent(e, this.props);
    const callback = this.props.onRate;

    callback && callback(rating);
  }

  _handleMouseLeave = () => {
    const callback = this.props.onRate;
    const state = this.state;

    if (state.rating === 0) {
      callback && callback(state.lastRating);
      this.setState({ rating: state.lastRating });
    }
  }

  _handleClick = (e) => {
    const rating = getRatingFromDOMEvent(e, this.props);
    const lastRating = Number(this.state.lastRating);
    const callback = this.props.onRate;

    if (e.target.getAttribute('class').indexOf('is-disabled') > -1) {
      return;
    }

    this.setState({ lastRating: rating, rating: rating });
    callback && callback(rating, lastRating);
  }
}
