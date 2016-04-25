
'use strict';

class TitleService {

  setTitle(pageNamePart: string): void {
    document.title = "B6 \u00BB " + pageNamePart;
  }
};

export default new TitleService();
