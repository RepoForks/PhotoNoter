package com.yydcdut.note.mvp.p.note.impl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.note.IZoomPresenter;
import com.yydcdut.note.mvp.v.note.IZoomView;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.UiHelper;

/**
 * Created by yuyidong on 15/11/15.
 */
public class ZoomPresenterImpl implements IZoomPresenter, Handler.Callback {
    private Context mContext;
    /* 数据 */
    private int mPosition;
    private int mComparator;
    private PhotoNote mPhotoNote;

    private IZoomView mZoomView;

    /* 图片有没有修改过 */
    private boolean mIsChanged = false;

    public ZoomPresenterImpl(String category, int position, int comparator) {
        mPosition = position;
        mComparator = comparator;
        mPhotoNote = PhotoNoteDBModel.getInstance().findByCategoryLabel(category, mComparator).get(mPosition);
    }

    /* Handler */
    private Handler mMainHandler;

    @Override
    public void attachView(IView iView) {
        mMainHandler = new Handler(this);
        mZoomView = (IZoomView) iView;
        mZoomView.showImage(mPhotoNote.getBigPhotoPathWithFile());
        mContext = NoteApplication.getContext();
    }

    @Override
    public void detachView() {

    }

    @Override
    public boolean handleMessage(Message msg) {
        mZoomView.hideProgressBar();
        return false;
    }

    @Override
    public void jump2PGEditActivity() {
        mZoomView.jump2PGEditActivity(mPhotoNote.getBigPhotoPathWithoutFile());
    }

    @Override
    public void refreshImage() {
        mZoomView.showImage(mPhotoNote.getBigPhotoPathWithFile());
    }

    @Override
    public void saveSmallImage(final Bitmap thumbNail) {
        mZoomView.showProgressBar();
        NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
            @Override
            public void run() {

                FilePathUtils.saveSmallPhotoFromSDK(mPhotoNote.getPhotoName(), thumbNail);

                mPhotoNote.setPaletteColor(UiHelper.getPaletteColor(ImageLoaderManager.loadImageSync(mPhotoNote.getBigPhotoPathWithFile())));
                PhotoNoteDBModel.getInstance().update(mPhotoNote);

                sendBroadcast();

                mMainHandler.sendEmptyMessage(1);

                mIsChanged = true;
            }
        });
    }

    @Override
    public void finishActivity() {
        mZoomView.finishActivity(mIsChanged);
    }

    private void sendBroadcast() {
        Intent intent = new Intent();
        intent.setAction(Const.BROADCAST_PHOTONOTE_UPDATE);
        intent.putExtra(Const.TARGET_BROADCAST_PHOTO, true);
        mContext.sendBroadcast(intent);
    }
}
