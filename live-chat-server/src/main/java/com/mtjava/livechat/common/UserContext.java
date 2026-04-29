package com.mtjava.livechat.common;

/**
 * 基于 ThreadLocal 保存当前请求的登录用户信息，由鉴权拦截器写入并在请求结束后清理。
 */
public final class UserContext {

    private static final ThreadLocal<AuthenticatedUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    /**
     * 在当前线程绑定登录用户信息。
     */
    public static void set(AuthenticatedUser user) {
        HOLDER.set(user);
    }

    /**
     * 获取当前线程里的登录用户；如果不存在，说明请求尚未通过鉴权。
     */
    public static AuthenticatedUser getRequired() {
        AuthenticatedUser user = HOLDER.get();
        if (user == null) {
            throw new BusinessException(ErrorCode.AUTH_REQUIRED);
        }
        return user;
    }

    /**
     * 清理线程上下文，避免线程复用时串号。
     */
    public static void clear() {
        HOLDER.remove();
    }
}
