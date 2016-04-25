'use strict';

jest.unmock('rsvp');
jest.dontMock('../service/CatalogAdapterService');

describe('catalog adapter', () => {
  let CatalogAdapterService;
  let EolaireServiceMock;

  beforeEach(() => {
    const rsvp = require('rsvp');

    const {default: eolaireServiceMock} = require('../service/EolaireService');
    EolaireServiceMock = eolaireServiceMock;
    // prepare entity list response for all the calls of getAllEntityTypes
    EolaireServiceMock.getEntityList.mockReturnValue(new rsvp.Promise((resolve, _) => {
      resolve({
        "types": [
          {"id": 1, "name": "author"},
          {"id": 20, "name": "book"}
        ]
      });
    }));

    const {default: catalogAdapterService} = require('../service/CatalogAdapterService');
    CatalogAdapterService = catalogAdapterService;
  });

  it('getAllEntityTypes', () => {
    // Given:
    let result = '<none>';

    // When:
    CatalogAdapterService.getAllEntityTypes().then(val => { result = val; });
    jest.runAllTimers(); // run all promises

    // Then:
    expect(result).toEqual({
      "author": 1,
      "book": 20
    });
  });
});