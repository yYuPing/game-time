import request from "../request";

/**
 * 获取当前登录用户的个人信息
 * @returns {Promise}
 */
export function getUserProfile() {
  return request({
    url: "/api/user/profile",
    method: "GET"
  });
}

/**
 * 更新用户个人信息
 * @param {Object} data - { email?, phone?, avatar? }
 * @returns {Promise}
 */
export function updateUserProfile(data) {
  return request({
    url: "/api/user/profile",
    method: "PUT",
    data
  });
}
