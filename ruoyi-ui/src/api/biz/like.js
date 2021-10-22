import request from '@/utils/request'

// 查询点赞列表
export function listLike(query) {
  return request({
    url: '/biz/like/list',
    method: 'get',
    params: query
  })
}

// 查询点赞详细
export function getLike(id) {
  return request({
    url: '/biz/like/' + id,
    method: 'get'
  })
}

// 新增点赞
export function addLike(id) {
  return request({
    url: '/biz/like/add/'+id,
    method: 'get',
  })
}

// 修改点赞
export function updateLike(data) {
  return request({
    url: '/biz/like',
    method: 'put',
    data: data
  })
}

// 删除点赞
export function delLike(id) {
  return request({
    url: '/biz/like/' + id,
    method: 'delete'
  })
}

// 导出点赞
export function exportLike(query) {
  return request({
    url: '/biz/like/export',
    method: 'get',
    params: query
  })
}
