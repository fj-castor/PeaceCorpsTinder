
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.useMasterKey();

/**
 * "string".format method
 */
if (!String.prototype.format) {
  String.prototype.format = function() {
    var args = arguments;
    return this.replace(/{(\d+)}/g, function(match, number) { 
      return typeof args[number] != 'undefined'
        ? args[number]
        : match
      ;
    });
  };
}
 
/**
 * Checks if array contains ParseObject by comparing id's.
 */
Array.prototype.containsObject = function(obj) {
    var i = this.length;
    while (i--) {
        if (this[i].id == obj.id) {
            return true;
        }
    }
    return false;
}
 
/**
 * FB API url generator
 */
function fb_api(user, endpoint, params) {
  var FB_BASE = "https://graph.facebook.com/v2.1/";
  var url = FB_BASE + endpoint + "?";
  if (typeof params !== "undefined") {
    url += encode_query(params);
  }
  url += '&access_token=' + user.get('authData').facebook.access_token;
  return url;
}
 
/**
 * Takes an object and encodes it for use as url params
 */
function encode_query(data) {
  var ret = [];
  for (var d in data)
    ret.push(encodeURIComponent(d) + "=" + encodeURIComponent(data[d]));
  return ret.join("&");
}
 
/**
 * Retrievies and saves the Facebook User's Facebook ID, name & picture into
 * the User object. Also pairs Installation with User.
 */
Parse.Cloud.define("registerUser", function(request, response) {
  var user = request.user;
 
  if (!user) {
    response.error("User not provided");
  } else if (Parse.FacebookUtils.isLinked(user)) {
    Parse.Cloud.httpRequest({
      url:fb_api(user, "me", {'fields':'id,first_name,last_name,name,picture.type(normal)'}),
      success:function(httpResponse) {
        var data = httpResponse.data;
         
        user.set('fbId', data.id);
        user.set('firstName', data.first_name);
        user.set('lastName', data.last_name);
        user.set('fullName', data.name);
        user.set('stealth', false);
 
        var installation = new Parse.Installation();
        installation.id = request.params.installationObjectId;
        user.set('installation', installation);
        installation.set('user', user);
 
        installation.save(null, {
          success: function(installation) {
            // fetch image
            Parse.Cloud.httpRequest({
              url:data.picture.data.url,
              success:function(httpResponse) {
                var picture = new Parse.File(data.id + ".jpg", {base64: httpResponse.buffer.toString('base64', 0, httpResponse.buffer.length)});
                user.set('picture', picture);
 
                user.save(null, {
                  success: function(user) {
                    response.success("User registration succeeded.");
                  },
                  error: function(user, error) {
                    response.error(error);
                  }
                });
              },
              error:function(httpResponse) {
                response.error(httpResponse);
              }
            });
          },
          error: function(installation, error) {
            response.error(error);
          }
        });
      },
      error:function(httpResponse) {
        response.error(httpResponse);
      }
    });
  }
});

function getPicture(user, callback) {
  Parse.Cloud.httpRequest({
    url: user.get('picture').url(),
    method: 'GET',
    success: function(httpResponse) {
      callback.success(httpResponse.buffer.toString('base64'));
    },
    error: function(httpResponse) {
      callback.error('Error getting image');
    }
  });
}
 
Parse.Cloud.define("getFriendPicture", function(request, response) {
  if (!request.params.userId) response.error('No userId specified.');
   
  var query = new Parse.Query(Parse.User);
  query.get(request.params.userId, {
    success: function(friend) {
      getPicture(friend, {
        success: function(buffer) {
          response.success(buffer);
        },
        error: function(error) {
          response.error(error);
        }
      });
    },
    error: function(friend, error) {
      response.error(error);
    }
  });
});
 
