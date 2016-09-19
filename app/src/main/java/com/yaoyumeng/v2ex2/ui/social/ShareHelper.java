package com.yaoyumeng.v2ex2.ui.social;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.yaoyumeng.v2ex2.R;

/**
 * Created by yw on 2015/5/17.
 */
public class ShareHelper {

    public static final String WEICHAT_APPID = "wx6042633e9bbd689c";
    public static final String WEICHAT_SECRET = "18eb7cce7003cd568097754b9f524d72";

    public static final String QQ_APPID = "100424468";
    public static final String QQ_APPKEY = "c7394704798a158208a74ab60104f0ba";

    private Activity mActivity;
    private String mTitle;
    private String mContent;
    private String mUrl;

    final UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share");

    SocializeListeners.SnsPostListener mSnsPostListener;

    public ShareHelper(Activity activity) {
        mActivity = activity;
        mSnsPostListener = new SocializeListeners.SnsPostListener() {
            public void onComplete(SHARE_MEDIA platform, int stCode,
                                   SocializeEntity entity) {
                if (stCode == 200)
                    Toast.makeText(mActivity, "分享成功", Toast.LENGTH_SHORT).show();
                else {
                    String eMsg = "";
                    if (stCode == -101) {
                        eMsg = "没有授权";
                    }
                    Toast.makeText(mActivity, "分享失败[" + stCode + "] " +
                            eMsg, Toast.LENGTH_SHORT).show();

                }
            }

            public void onStart() {
            }
        };
    }

    public void setContent(String title, String content, String url) {
        mTitle = title;
        mContent = content;
        mUrl = url;
    }

    protected UMImage getShareImg() {
        UMImage img = new UMImage(mActivity, R.drawable.ic_launcher);
        return img;
    }

    /**
     * 分享
     */
    public void handleShare() {
        final ShareDialog dialog = new ShareDialog(mActivity);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle(R.string.share_to);
        dialog.setOnPlatformClickListener(new ShareDialog.OnSharePlatformClick() {
            @Override
            public void onPlatformClick(int id) {
                switch (id) {
                    case R.id.ly_share_weichat_circle:
                        shareToWeiChatCircle();
                        break;
                    case R.id.ly_share_weichat:
                        shareToWeiChat();
                        break;
                    case R.id.ly_share_sina_weibo:
                        shareToSinaWeibo();
                        break;
                    case R.id.ly_share_qq:
                        shareToQQ(SHARE_MEDIA.QQ);
                        break;
                    case R.id.ly_share_copy_link:
                        copyTextToBoard(mUrl);
                        break;
                    case R.id.ly_share_more_option:
                        shareContentWithSystem();
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void shareContentWithSystem() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + mContent);
        intent.putExtra(Intent.EXTRA_TEXT, mContent + " " + mUrl);
        mActivity.startActivity(Intent.createChooser(intent, "选择分享"));
    }

    private void copyTextToBoard(String string) {
        ClipboardManager clip = (ClipboardManager) mActivity
                .getSystemService(Context.CLIPBOARD_SERVICE);
        clip.setText(string);
        Toast.makeText(mActivity, "成功复制到剪贴板", Toast.LENGTH_SHORT).show();
    }

    private void shareToWeiChatCircle() {
        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(mActivity, WEICHAT_APPID, WEICHAT_SECRET);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        // 设置微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(mContent);
        // 设置朋友圈title
        circleMedia.setTitle(mContent);
        circleMedia.setShareImage(getShareImg());
        circleMedia.setTargetUrl(mUrl);
        mController.setShareMedia(circleMedia);
        mController.postShare(mActivity, SHARE_MEDIA.WEIXIN_CIRCLE, mSnsPostListener);
    }

    private void shareToWeiChat() {
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(mActivity, WEICHAT_APPID, WEICHAT_SECRET);
        wxHandler.addToSocialSDK();
        // 设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        // 设置分享文字
        weixinContent.setShareContent(mContent);
        // 设置title
        weixinContent.setTitle(mTitle);
        // 设置分享内容跳转URL
        weixinContent.setTargetUrl(mUrl);
        // 设置分享图片
        weixinContent.setShareImage(getShareImg());
        mController.setShareMedia(weixinContent);
        mController.postShare(mActivity, SHARE_MEDIA.WEIXIN, mSnsPostListener);
    }

    private void shareToSinaWeibo() {
        SinaShareContent weiboContent = new SinaShareContent();
        weiboContent.setTitle(mTitle);
        weiboContent.setShareContent(mContent + " " + mUrl + "（分享自@V2EX客户端）");
        weiboContent.setShareImage(getShareImg());
        mController.setShareMedia(weiboContent);
        mController.postShare(mActivity, SHARE_MEDIA.SINA, mSnsPostListener);
    }

    protected void shareToQQ(SHARE_MEDIA media) {
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mActivity,
                QQ_APPID, QQ_APPKEY);
        qqSsoHandler.setTargetUrl(mUrl);
        qqSsoHandler.setTitle(mTitle);
        qqSsoHandler.addToSocialSDK();
        mController.setShareContent(mContent);
        mController.setShareImage(getShareImg());
        mController.postShare(mActivity, media, null);
    }
}
