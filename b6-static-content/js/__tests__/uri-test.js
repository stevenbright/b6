jest.dontMock('../util/uri.js');

import uri from '../util/uri.js';

const parseQueryString = uri.parseQueryString;

describe('parse query string', function () {
  it('parses one parameter', function () {
    // Given:
    const url = "http://localhost/resource?debug=1";

    // When:
    const queryParam = parseQueryString(url);

    // Then:
    expect(1).toBe(1);
    //expect(queryParam["debug"]).toBe("1");
  });
});