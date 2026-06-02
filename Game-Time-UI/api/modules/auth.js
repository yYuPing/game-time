import request from "../request";

/**
 * 登录
 * @param {string} username
 * @param {string} password
 * @returns {Promise}
 */
export function loginAPI(username, password) {
  return request({
    url: "/api/auth/login",
    method: "POST",
    data: { username, password }
  });
}

/**
 * 注册
 * @param {string} username
 * @param {string} password
 * @param {string} email
 * @returns {Promise}
 */
export function registerAPI(username, password, email) {
  return request({
    url: "/api/auth/register",
    method: "POST",
    data: { username, password, email }
  });
}
