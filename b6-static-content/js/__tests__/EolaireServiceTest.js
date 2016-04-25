'use strict';

jest.mock('rsvp-ajax');
jest.unmock('rsvp');
jest.dontMock('../util/AppConstants');
jest.dontMock('../service/EolaireService');

describe('catalog service test', () => {
  let ajax;
  let EolaireService;

  beforeEach(() => {
    ajax = require('rsvp-ajax');
    ajax.__clearMocks();

    const {default: service} = require('../service/EolaireService');
    EolaireService = service;
  });

  it('getEntityList', () => {
    // Given:
    const expectedVal = 100;
    const offsetToken = 'offsetToken-0';
    const limit = 2;
    let capturedRequest = '<request?>';
    ajax.__putMockResponse('POST', '/rest/eolaire/entity/list', request => {
      capturedRequest = request;
      return expectedVal;
    });

    // When:
    let actualVal = '<val?>';
    EolaireService.getEntityList(offsetToken, limit).then(val => { actualVal = val; });
    jest.runAllTimers(); // run all promises

    // Then:
    expect(actualVal).toBe(expectedVal);
    expect(capturedRequest).toEqual({"offsetToken": offsetToken, "limit": limit});
  });

  it('getItemById', () => {
    // Given:
    const expectedVal = 100;
    ajax.__putMockResponse('GET', '/rest/eolaire/item/entry/1', expectedVal);

    // When:
    let actualVal = '<none>';
    EolaireService.getItemById(1).then(val => { actualVal = val; });
    jest.runAllTimers(); // run all promises

    // Then:
    expect(actualVal).toBe(expectedVal);
  });

  it('getItemProfile', () => {
    // Given:
    const expectedVal = 100;
    ajax.__putMockResponse('GET', '/rest/eolaire/item/profile/2', expectedVal);

    // When:
    let actualVal = '<none>';
    EolaireService.getItemProfile(2).then(val => { actualVal = val; });
    jest.runAllTimers(); // run all promises

    // Then:
    expect(actualVal).toBe(expectedVal);
  });

  it('getItemByType', () => {
    // Given:
    const expectedVal = 100;
    const itemTypeId = 1;
    const offsetToken = 'offsetToken-0';
    const limit = 2;
    let capturedRequest = '<request?>';
    ajax.__putMockResponse('POST', '/rest/eolaire/item/query/by-type', request => {
      capturedRequest = request;
      return expectedVal;
    });

    // When:
    let actualVal = '<val?>';
    EolaireService.getItemByType(itemTypeId, offsetToken, limit).then(val => { actualVal = val; });
    jest.runAllTimers(); // run all promises

    // Then:
    expect(actualVal).toBe(expectedVal);
    expect(capturedRequest).toEqual({"itemTypeId": itemTypeId, "offsetToken": offsetToken, "limit": limit});
  });

  it('getItemRelations', () => {
    // Given:
    const expectedVal = 100;
    const itemId = 10;
    const filterMode = 'filterMode-0';
    let capturedRequest = '<request?>';
    ajax.__putMockResponse('POST', '/rest/eolaire/item/relations', request => {
      capturedRequest = request;
      return expectedVal;
    });

    // When:
    let actualVal = '<val?>';
    EolaireService.getItemRelations(itemId, filterMode).then(val => { actualVal = val; });
    jest.runAllTimers(); // run all promises

    // Then:
    expect(actualVal).toBe(expectedVal);
    expect(capturedRequest).toEqual({"itemId": itemId, "relationsFilterMode": filterMode});
  });
});
