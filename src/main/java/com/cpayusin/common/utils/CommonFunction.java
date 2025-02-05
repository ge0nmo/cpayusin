package com.cpayusin.common.utils;

import jakarta.servlet.http.HttpServletRequest;

public class CommonFunction
{
    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = getIpFromHeaders(request);

        if (isInvalidIpAddress(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        ipAddress = extractClientIpAddressFromLB(ipAddress);

        return ipAddress;
    }

    private static String getIpFromHeaders(HttpServletRequest request) {
        String[] headersToCheck = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR",
        };

        for (String header : headersToCheck) {
            String ip = request.getHeader(header);
            if (!isInvalidIpAddress(ip)) {
                return ip;
            }
        }
        return null;
    }

    private static boolean isInvalidIpAddress(String ipAddress) {
        return ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress);
    }

    private static String extractClientIpAddressFromLB(String ipAddress) {
        if (ipAddress != null && ipAddress.contains(",")) {
            return ipAddress.split(",")[0];
        }
        return ipAddress;
    }

}
