distributionModule.service('SharedDistributions', function (IndexedDB, $rootScope) {

  this.distributionList = [];

  var thisService = this;

  this.update = function () {
    IndexedDB.transaction(function (connection) {
      var transaction = connection.transaction('distributions');

      var cursorRequest = transaction.objectStore('distributions').openCursor();
      var aggregate = [];

      cursorRequest.onsuccess = function (event) {
        var cursor = event.target.result;
        if (cursor) {
          aggregate.push(cursor.value);
          cursor['continue']();
        }
      };

      transaction.oncomplete = function (e) {
        thisService.distributionList = aggregate;
        $rootScope.$apply();
      };
    });
  };

  this.update();

});
