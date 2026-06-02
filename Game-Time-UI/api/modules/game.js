import request from "../request";

/**
 * 获取游戏列表
 * @returns {Promise}
 */
export function getGameList() {
  return request({
    url: "/api/games",
    method: "GET"
  });
}
