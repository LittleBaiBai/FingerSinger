package com.game.fingersinger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.game.fingersinger.LongPressButton.RepeatListener;

import android.text.format.Time;
import android.util.Log;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class MainActivity extends Activity implements RepeatListener {

	private String historyPath, audioPath;
	private String fileName = "";
	private String tempName = "";

	private DrawLines drawView;
	private MediaRecorder mediaRecord;
	private Thread playCurrentThr, playWholeThr;
	private ImageButton statusBtn, colorBtn, undoBtn, menuBtn, playBtn;
	private LongPressButton	pageLeft, pageRight;
	private ImageView pointer;
	private EditText edit;
	private static ListView listview;
	private static RelativeLayout menuPad;
	private RelativeLayout voiceBarLayout;
	protected boolean inScrollArea;
	public int inScroll;
	public float mX = 0, mY = 0;
	protected boolean validClick;
	private boolean isPlaying;
	
	protected static final int HISTORYDIALOG = 0x111;
	protected static final int IMPORTDIALOG = 0x112;
	protected static final int NEWCANVASDIALOG = 0x113;
	protected static final int RECORDDIALOG = 0x114;
	protected static final int SAVEONLY = 0x115;
	protected static final int HELPDIALOG = 0x116;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//隐去标题栏（应用程序的名字）  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐去状态栏部分(电池等图标和一切修饰部分)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设定调整音量为媒体音量,当暂停播放的时候调整音量就不会再默认调整铃声音量了

        this.setContentView(R.layout.activity_main);
   //     new Thread(new moveCanvasThread()).start();
        getDirPath();   
       	initView(); 
	}	

	private void initView() {
		
		setContentView(R.layout.activity_main);

		LinearLayout drawLayout = (LinearLayout) findViewById(R.id.view_draw);
		pointer = (ImageView)findViewById(R.id.pointer);
		drawView = new DrawLines(this, pointer);
		drawView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));  
		drawLayout.addView(drawView);

		menuPad = (RelativeLayout) findViewById(R.id.menu_pad);
		voiceBarLayout = (RelativeLayout) findViewById(R.id.seekbars);
		listview = (ListView)this.findViewById(R.id.list_view);
		
		statusBtn = (ImageButton) findViewById(R.id.button_status);
	    colorBtn = (ImageButton) findViewById(R.id.button_color);
	    menuBtn = (ImageButton) findViewById(R.id.button_menu);
	    playBtn = (ImageButton) findViewById(R.id.button_play_current);
		undoBtn = (ImageButton) findViewById(R.id.button_undo);
		pageLeft = (LongPressButton) findViewById(R.id.button_page_left);
		pageRight = (LongPressButton) findViewById(R.id.button_page_right);
		
		statusBtn.setOnClickListener(new OnClickListener() {  
			
	        public void onClick(View v) {  
	        	closeOutdatedViews();
				
//	            Log.v("Button","Click on Menu");
	            if(Declare.menu_status == 1){
	            	statusBtn.setImageDrawable(getResources().getDrawable(R.drawable.xml_status_eraser));
	            	Declare.menu_status = 2;
					Toast.makeText(MainActivity.this, R.string.prompt_status_eraser, Toast.LENGTH_SHORT).show();
				}
				else if (Declare.menu_status == 2) {
					statusBtn.setImageDrawable(getResources().getDrawable(R.drawable.xml_status_voice));
					Declare.menu_status = 3;
					Toast.makeText(MainActivity.this, R.string.prompt_status_voice, Toast.LENGTH_SHORT).show();

					voiceBarLayout = (RelativeLayout) findViewById(R.id.seekbars);
				    voiceBarLayout.setVisibility(View.VISIBLE);
				    
				    changeVoice();
				}
				else if (Declare.menu_status == 3) {
				    voiceBarLayout.setVisibility(View.GONE);
				    
				    statusBtn.setImageDrawable(getResources().getDrawable(R.drawable.xml_status_draw));
					Declare.menu_status = 1;
					Toast.makeText(MainActivity.this, R.string.prompt_status_draw, Toast.LENGTH_SHORT).show();
				}	
				else {
					Log.v("somethingStrange", "menu_status: " + Declare.menu_status);
                }
				
	       }

			private void changeVoice() {
				// TODO Auto-generated method stub
				SeekBar seek1 = (SeekBar)findViewById(R.id.seekBar1);
				SeekBar seek2 = (SeekBar)findViewById(R.id.seekBar2);
				SeekBar seek3 = (SeekBar)findViewById(R.id.seekBar3);
				SeekBar seek4 = (SeekBar)findViewById(R.id.seekBar4);
				SeekBar seek5 = (SeekBar)findViewById(R.id.seekBar5);
				
				seek1.setOnSeekBarChangeListener(new ProgressSeekListener(seek1, 0, drawView.melody[0]));
				seek2.setOnSeekBarChangeListener(new ProgressSeekListener(seek2, 1, drawView.melody[1]));
				seek3.setOnSeekBarChangeListener(new ProgressSeekListener(seek3, 2, drawView.melody[2]));
				seek4.setOnSeekBarChangeListener(new ProgressSeekListener(seek4, 3, drawView.melody[3]));
				seek5.setOnSeekBarChangeListener(new ProgressSeekListener(seek5, 4, drawView.melody[4]));
			}
	            
	    }); 
		
		colorBtn.setOnClickListener(new OnClickListener() {  
	        
	        public void onClick(View v) { 
	        	closeOutdatedViews();
	        	
//	            Log.v("Button","Click on Color");
	            Declare.menu_status = 1;
	            statusBtn.setImageDrawable(getResources().getDrawable(R.drawable.xml_status_draw));
	            if (Declare.color_status == 0) {
	            	colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.xml_color_yellow));
	    			Declare.soundManager[1].playSound(10, drawView.melody[1].voice);
					Declare.color_status = 1;
	            }
	            else if(Declare.color_status == 1){
	            	colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.xml_color_purple));
	            	Declare.soundManager[2].playSound(10, drawView.melody[2].voice);
	            	Declare.color_status = 2;
				}
	            else if (Declare.color_status == 2){
	            	colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.xml_color_orange));
	            	Declare.soundManager[3].playSound(10, drawView.melody[3].voice);
	            	Declare.color_status = 3;
				}
	            else if (Declare.color_status == 3){
	            	colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.xml_color_green));
	            	Declare.soundManager[4].playSound(10, drawView.melody[4].voice);
	            	Declare.color_status = 4;
				}
				else {
	            	colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.xml_color_blue));
	            	Declare.soundManager[0].playSound(10, drawView.melody[0].voice);
	            	Declare.color_status = 0;
				}	
	        }    
	    }); 
		
		playBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				closeOutdatedViews();
				
				if (!isPlaying) {
		        	prepareForPlay();
					playCurrentThr = new Thread(new Runnable(){			
						@Override
						public void run() {
							playSong((Declare.pointerInScreen + drawView.melody_start) / Declare.tempo_length);  				
						}
					});
					playCurrentThr.start();
				}
				else {
	            	Message message = new Message(); 
                    message.what = PLAYSTOP;                    
                    myHandler.sendMessage(message);
				}
			}
		});
		
		undoBtn.setOnClickListener(new OnClickListener() {  
	        
	        public void onClick(View v) { 
	        	closeOutdatedViews();
//	            Log.v("Button","Click on Undo");
	            
	            if (Declare.menu_status != 4 ) {
	            	drawView.undo();	            	
	            }
	        }    
	    }); 
		
		menuBtn.setOnClickListener(new OnClickListener() {

	        public void onClick(View v) { 
	        	listview.setVisibility(View.GONE);
	        	if (menuPad.getVisibility() == View.GONE) {
	        		menuPad.setVisibility(View.VISIBLE);
	        		drawView.setClickable(false);
	        		ImageButton mPlayWhole = (ImageButton) findViewById(R.id.menu_play_whole);
	        		ImageButton mSave = (ImageButton) findViewById(R.id.menu_save);
	        		ImageButton mNew = (ImageButton) findViewById(R.id.menu_new);
	        		ImageButton mHistory = (ImageButton) findViewById(R.id.menu_history);
	        		ImageButton mExport = (ImageButton) findViewById(R.id.menu_audio_export);
	        		ImageButton mRender = (ImageButton) findViewById(R.id.menu_audio_render);
	        		ImageButton mImport = (ImageButton) findViewById(R.id.menu_image_import);
	        		ImageButton mHelp = (ImageButton) findViewById(R.id.menu_help);
	        		ImageButton mAbout = (ImageButton) findViewById(R.id.menu_about);
	        		
	        		mPlayWhole.setOnClickListener(new OnClickListener() {
	        			 
	        	        public void onClick(View v) { 
	        	        	closeOutdatedViews();
	    		        	prepareForPlay();
	        				playWholeThr = new Thread(new Runnable(){			
	        					@Override
	        					public void run() {
	        						playSong(0);  
	        					}
	        				});
	        				playWholeThr.start();
	        	        }  
	        		});
	        		
	        		mSave.setOnClickListener(new OnClickListener() {
	        			 
	        	        public void onClick(View v) { 
	        	        	closeOutdatedViews();
	        	        	if (Declare.isSaved == true) return;
	        				new SaveDialog(SAVEONLY);
	        	        } 
	        		});
	        		
	        		mNew.setOnClickListener(new OnClickListener() {
	        			 
	        	        public void onClick(View v) { 
	        	        	closeOutdatedViews();
	        	        	new SaveDialog(NEWCANVASDIALOG);
	        	        }
	        		});

	        		mHistory.setOnClickListener(new OnClickListener() {
	        			 
	        	        public void onClick(View v) { 
	        	        	closeOutdatedViews();
	        	        	new SaveDialog(HISTORYDIALOG);
	        	        }
	        		});

	        		mExport.setOnClickListener(new OnClickListener() {
	        			 
	        	        public void onClick(View v) { 
	        	        	closeOutdatedViews();
	        	        	new SaveDialog(RECORDDIALOG);
	        	        }  
	        		});

	        		mRender.setOnClickListener(new OnClickListener() {
	        			 
	        	        public void onClick(View v) { 
	        	        	closeOutdatedViews();
	    		        	Log.v("menuItem", "audio render");
	    					Toast.makeText(MainActivity.this, "此功能尚未完成，敬请期待", Toast.LENGTH_SHORT).show();
	        	        }  
	        		});

	        		mImport.setOnClickListener(new OnClickListener() {
	        			 
	        	        public void onClick(View v) { 
	        	        	closeOutdatedViews();
	        	        	new SaveDialog(IMPORTDIALOG);
	        	        }

	        		});

	        		mHelp.setOnClickListener(new OnClickListener() {
	        			 
	        	        public void onClick(View v) { 
	        	        	closeOutdatedViews();
	        	        	new SaveDialog(HELPDIALOG);
	        	        }  
	        		});

	        		mAbout.setOnClickListener(new OnClickListener() {
	        			 
	        	        public void onClick(View v) { 
	        	        	closeOutdatedViews();
	        	        	ImageView about = (ImageView) findViewById(R.id.about);

	        	    		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);  
	        	    		aa.setDuration(2000);
	        	    		about.setAnimation(aa);
	        	    		
	        	        	about.setVisibility(View.VISIBLE);
	        	        	about.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
			        	    		AlphaAnimation aa = new AlphaAnimation(1.0f, 0.0f);  
			        	    		aa.setDuration(2000);
			        	    		arg0.setAnimation(aa);
			        	    		
									arg0.setVisibility(View.GONE);
								}
	        	        		
	        	        	});
	        	        }  
	        		});
	        		
	        	}
	        	else {
	        		menuPad.setVisibility(View.GONE);
	        	}
	        }  
		});
		
		pageLeft.setRepeatListener(this, -1);
		pageRight.setRepeatListener(this, 1);
	} 
	
	//图库返回结果处理
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v("onActivityResult", "requestCode/resultCode: " + requestCode + "/" + resultCode);
		if (requestCode == 0 && resultCode == RESULT_OK && null != data) {
			Bitmap cameraBitmap = (Bitmap) data.getExtras().get("data");
			
			newCanvas(false);
			BitmapDrawable background = new BitmapDrawable(cameraBitmap);
			
			int height = background.getIntrinsicHeight();
			int width = background.getIntrinsicWidth();
			Log.v("onActivityResult", "image: " + width + "/" + height);
			RelativeLayout main = (RelativeLayout) findViewById(R.id.main_layout);
			main.setBackgroundDrawable(background);
		}
	}

	//清空画布，参数代表是否将背景重置
	private void newCanvas(boolean freshBackground) {	
		if (freshBackground) {
			RelativeLayout main = (RelativeLayout) findViewById(R.id.main_layout);
			main.setBackgroundDrawable(getResources().getDrawable(R.drawable.background));
		}
    	fileName = "";
		//清空画布要做的事！！
		Log.v("clearCanvas", "mNew");
		drawView.deleteCanvas();
	}  

	//选择图库中的图片
	private void pickImage() {
//		Intent i = new Intent(Intent.ACTION_PICK, 
//    			android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//调用android的图库
//    	startActivityForResult(i, 2);
		Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
	    intent.addCategory(Intent.CATEGORY_OPENABLE);
	    intent.setType("image/*");
	    intent.putExtra("crop", "true");
	    intent.putExtra("aspectX", 800);
	    intent.putExtra("aspectY", 480);
	    intent.putExtra("outputX", 800);
	    intent.putExtra("outputY", 480);
	    intent.putExtra("return-data", true);

	    startActivityForResult(intent, 0);
	}  
	
	//开始播放前，将图标置为不可点击状态
	private void prepareForPlay() {
		menuBtn.setClickable(false);
		colorBtn.setClickable(false);
		statusBtn.setClickable(false);
		undoBtn.setClickable(false);
		
		playBtn.setImageDrawable(getResources().getDrawable(R.drawable.xml_stop));
		undoBtn.setImageDrawable(getResources().getDrawable(R.drawable.unclickable_undo));
		menuBtn.setImageDrawable(getResources().getDrawable(R.drawable.unclickable_main_menu));
		switch (Declare.color_status) {
		case 0:
			colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.unclickable_color_blue));
			break;
		case 1:
			colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.unclickable_color_yellow));
			break;
		case 2:
			colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.unclickable_color_purple));
			break;
		case 3:
			colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.unclickable_color_orange));
			break;
		case 4:
			colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.unclickable_color_green));
			break;	
		}
		
		switch (Declare.menu_status) {
		case 1:
			statusBtn.setImageDrawable(getResources().getDrawable(R.drawable.unclickable_status_draw));
			break;
		case 2:
			statusBtn.setImageDrawable(getResources().getDrawable(R.drawable.unclickable_status_eraser));
			break;
		case 3:
			statusBtn.setImageDrawable(getResources().getDrawable(R.drawable.unclickable_status_voice));
			break;
		}
	}

	//播放完成后，图标恢复正常
	private void afterPlay() {
		this.isPlaying = false;
		menuBtn.setClickable(true);
		colorBtn.setClickable(true);
		statusBtn.setClickable(true);
		undoBtn.setClickable(true);
		
		undoBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_undo));
		menuBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_main_menu));
		switch (Declare.color_status) {
		case 0:
			colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_color_blue));
			break;
		case 1:
			colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_color_yellow));
			break;
		case 2:
			colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_color_purple));
			break;
		case 3:
			colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_color_orange));
			break;
		case 4:
			colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_color_green));
			break;	
		}
		
		switch (Declare.menu_status) {
		case 1:
			statusBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_status_draw));
			break;
		case 2:
			statusBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_status_eraser));
			break;
		case 3:
			statusBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_status_voice));
			break;
		}
	}

	//获取路径、创建文件夹
	private void getDirPath() {
		String dirPath;
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			dirPath = Environment.getExternalStorageDirectory().getPath();
		}
		else {
			historyPath = "";
			audioPath = "";
			promptNoSdcard();
			return;
		}
		historyPath = dirPath + "/FingerSinger/history/";
		audioPath = dirPath + "/FingerSinger/output/";;
		
		File f1 = new File(this.historyPath); //historyPath为历史曲目路径
		if (!f1.exists()) {
			f1.mkdirs();	//建立文件夹 
		}
		
		File f2 = new File(this.audioPath); //audioPath为声音导出路径
		if (!f2.exists()) {
			f2.mkdirs();	//建立文件夹 
		}

	}	
	
	//提示用户没有SD卡
	private void promptNoSdcard() {
		Toast.makeText(MainActivity.this, "No Sdcard!", Toast.LENGTH_SHORT).show();
	}

	//导出音频，将其录一遍
	private void recordSong() {	
		if (this.audioPath.equals("")) {
			this.promptNoSdcard();
			return;
		}
		mediaRecord = new MediaRecorder();
		//mediaRecord.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);	//设置音频源
		mediaRecord.setAudioSource(MediaRecorder.AudioSource.DEFAULT);	//设置音频源
		mediaRecord.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);	//设置音频输出格式
		mediaRecord.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);	//设置音频编码
		
		Time t = new Time();
		t.setToNow(); 
		
		mediaRecord.setOutputFile(audioPath + "/" + fileName + "_" + t.year + t.month + t.monthDay + t.hour + t.minute + t.second + ".mp3");
		try {
			mediaRecord.prepare();
			mediaRecord.start();
			Declare.menu_status = 4;
			playSong(0);
			Declare.menu_status = 1;

			mediaRecord.stop();
			Toast.makeText(MainActivity.this, "保存在/sdcard/FingerSinger/output/目录下", Toast.LENGTH_SHORT).show();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			Toast.makeText(MainActivity.this, "录制失败", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(MainActivity.this, "录制失败", Toast.LENGTH_SHORT).show();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mediaRecord.release();
	}

	//从历史纪录中恢复曲目
	private void loadFromHistory() {
		if (this.historyPath.equals("")) {
			this.promptNoSdcard();
			return;
		}
		try{
			String temp = fileName;
			newCanvas(true);
			fileName = temp;
			//从文件中恢复
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(historyPath + "/" + fileName + ".psong"));

			drawView.melody = (Melody[])in.readObject();
			in.close();
			
			//重画画布
			drawView.reDraw();
			Declare.isSaved = true;
		}
		catch(Exception e){
	       e.printStackTrace();
        }
		
	}

	//显示历史文件列表
	@SuppressLint("SimpleDateFormat")
	private void showFileList() {	
		List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		listview.setBackgroundColor(Color.GRAY);
		File f = new File(this.historyPath); //strPath为路径
		//File f = new File(Environment.getRootDirectory().getPath()); //strPath为路径

		//Log.v("debuga", f.getPath());
		if (!(f.exists())) {
//			Log.v("debuga", "not exists");
			Dialog dialog = new AlertDialog.Builder(MainActivity.this)  
				//设置对话框要显示的消息  
				.setMessage("无历史曲目")  
				//设置按钮
				.setPositiveButton("确定", new DialogInterface.OnClickListener(){  
					public void onClick(DialogInterface dialog, int which) {  
						//好像什么都不用干
					}  
				}).create();
			dialog.show();
			return;
		}
		else {
			String[] file_list = f.list(); //String[] 
			if (file_list.length == 0) {
				Dialog dialog = new AlertDialog.Builder(MainActivity.this)  
					//设置对话框要显示的消息  
					.setMessage("无历史曲目")  
					//设置按钮
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
						public void onClick(DialogInterface dialog, int which) {  
							//好像什么都不用干
						}
					}).create();
				dialog.show();
				return;
			}
			else {
				SimpleDateFormat dateformat1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
//				Log.v("debuga", "file_list:" + f.exists());
				for (int i = 0; i < file_list.length; i++) {
					if (file_list[i].endsWith(".psong")) {
						HashMap<String, String> info = new HashMap<String, String>();
//						Log.v("debuga", "aaa:" + dateformat1.format(((new File(historyPath + "/" + file_list[i])).lastModified())) + " length:" + file_list[i]);
						info.put("name", file_list[i].substring(0, file_list[i].length() - 6));
						info.put("time", dateformat1.format(((new File(historyPath + "/" + file_list[i])).lastModified())));
						data.add(info);
					}
				}
			}
		}
		
		SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, data, R.layout.item_menu, 
				new String[] {"name", "time"}, new int[] {R.id.name, R.id.time});
		listview.setAdapter(adapter);
		listview.bringToFront();
		listview.setVisibility(View.VISIBLE);
		Toast.makeText(MainActivity.this, "LongClick on item to delete", Toast.LENGTH_SHORT).show();
		
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ListView lv = (ListView)arg0;
				@SuppressWarnings("unchecked")
				HashMap<String, String> tem = (HashMap<String, String>)lv.getItemAtPosition(arg2);
				tempName = tem.get("name");
				lv.setVisibility(View.GONE);
				Dialog isDeleteDialog = new AlertDialog.Builder(MainActivity.this).setMessage("是否删除本曲目？")
						//设置按钮
						.setPositiveButton("删了吧", new DialogInterface.OnClickListener(){ 
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								File f = new File(historyPath + "/" + tempName + ".psong");
								f.delete();
							}  
						})
						.setNegativeButton("不要删", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//好像什么都不用做
							}
						})
						.setCancelable(true).create();
					isDeleteDialog.show();
				return false;
			}
		});
	
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ListView lv = (ListView)arg0;
				@SuppressWarnings("unchecked")
				HashMap<String, String> tem = (HashMap<String, String>)lv.getItemAtPosition(arg2);
				fileName = tem.get("name");
				lv.setVisibility(View.GONE);
				//Toast.makeText(MainActivity.this, "Click" + fileName, 200).show();
				loadFromHistory();
			}
		});
	}

	//在点击按钮后，将屏幕上多余的控件清理掉
	private void closeOutdatedViews() {
		voiceBarLayout.setVisibility(View.GONE);  
		closeSomeViews();
	}

	public static void closeSomeViews() {
		listview.setVisibility(View.GONE);
		menuPad.setVisibility(View.GONE);
	}
	
	private void startGuide() {
		Intent intent = new Intent(MainActivity.this, GuideActivity.class);
//      intent.putExtra("melody", drawView.melody);
		MainActivity.this.startActivity(intent); 
//		MainActivity.this.finish();
	}
	
	//保存曲目要做的事
	private void onSave() throws IOException {
		if (this.historyPath.equals("")) {
			this.promptNoSdcard();
			return;
		}
		
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(historyPath + "/" + fileName + ".psong"));
		out.writeObject(drawView.melody);
		out.close();
		Declare.isSaved = true;
		Toast.makeText(MainActivity.this, "Already Saved", Toast.LENGTH_SHORT).show();
	}
	
	//从tempoId=start起播放声音至结尾
	private void playSong(int start) {
		this.isPlaying = true;
		//确认乐曲的开头
		if (start == 0) {
			start = Declare.screen_width;
			Log.v("pstart", "here");
			for (int i = 0; i < 5; i++) {
				if (drawView.melody[i].starts.size() == 0) continue;
				int temp = drawView.melody[i].starts.get(0);
//				Log.v("pstart", "i: start/temp: " + i + ": " + start + "/" + temp);
				if (temp < start) {
					start = temp;
				}
			}
			if (start == Declare.screen_width) start = 0;
		}
		//确认乐曲的结尾
		int end = 0;
		for (int i = 0; i < 5; i++) {
			Log.v("playSong", "color: " + i + " size:" + drawView.melody[i].stops.size());
			if (drawView.melody[i].stops.size() == 0) continue;
			int temp = drawView.melody[i].stops.get(drawView.melody[i].stops.size() - 1);
			if (temp > end) {
				end = temp;
			}
		}
		//如果是播放整曲则将画布挪到乐曲开头
		Log.v("playSong", "before for loop：" + start + "/" + end);
		if (start * Declare.tempo_length < drawView.melody_start) {
			drawView.melody_start = start * Declare.tempo_length;
			Message message = new Message(); 
	        message.what = MOVECANVASTOSTART;                    
	        myHandler.sendMessage(message);
		}
		Log.v("sing a song", "start!");
		//从start到end遍历播音，每播一格挪动一格指针或画布
		for (int i = start; i <= end; i++) {
			for (int j = 0; j < 5; j++) {
				if (drawView.melody[j].notes.isEmpty()) continue;
				if (drawView.melody[j].stops.get(drawView.melody[j].stops.size() - 1) < i) continue;
				int note = drawView.melody[j].notes.get(i);
//				Log.v("playSong", start + "/" + end + " note: " + note);
				if (isPlaying == false) {
					break;
				}
				if (note == 0) {
					Declare.soundManager[j].playSound(0, drawView.melody[j].voice);
				}
				else {
					Declare.soundManager[j].playSound(Declare.getIndexOfSound(note), drawView.melody[j].voice);
				}
				if (i > 0) {
					Message message = new Message(); 
			        message.what = MOVEPOINTER;   
			        message.arg1 = i * Declare.tempo_length - drawView.melody_start;                 
			        myHandler.sendMessage(message);
				}
			}	
			if (isPlaying == false) {
				break;
			}
			try {
				Thread.sleep((long) (500 * Declare.speed));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Message message = new Message(); 
        message.what = PLAYSTOP;                    
        myHandler.sendMessage(message);
		this.isPlaying = false;	
	}

	protected static final int PLAYSTOP = 0x101; 
	protected static final int PLAYPAUSE = 0x102;
	protected static final int PLAYRESUME = 0x103;
	protected static final int MOVEPOINTER = 0x104;
	protected static final int MOVECANVASTOSTART = 0x105;
//	protected static final int MOVECANVASALITTLE = 0x106;
	@SuppressLint("HandlerLeak")
	Handler myHandler = new Handler() {
        public void handleMessage(Message msg) { 
             switch (msg.what) { 
             case PLAYSTOP: 
            	 isPlaying = false;
            	 playBtn.setImageDrawable(getResources().getDrawable(R.drawable.xml_play_current));
            	 Toast.makeText(MainActivity.this, R.string.prompt_stop_play, Toast.LENGTH_SHORT).show();
            	 Declare.pointerInScreen = 0;
            	 afterPlay();
            	 break;
             case MOVEPOINTER:
            	 int pointerInScreen = msg.arg1;
            	 if (pointerInScreen < Declare.screen_width - 200) {
            		 Animation  animation = new TranslateAnimation(pointerInScreen - Declare.pointer_unpress/2, (pointerInScreen + Declare.tempo_length - Declare.pointer_unpress/2), 0, 0);
                     animation.setDuration((long) (500 * Declare.speed));
                     animation.setFillAfter(false);
                 	 pointer.startAnimation(animation);
                 	 Declare.pointerInScreen = (int)(pointerInScreen - Declare.pointer_unpress/2);
         		 }
                 else {
//                 	 Log.v("InScroll","right");
                	 drawView.melody_start += 40;
                  	 drawView.reDraw();
                 	 	
                	 Animation animation = new TranslateAnimation(pointerInScreen - Declare.pointer_unpress/2, (pointerInScreen - Declare.pointer_unpress/2), 0, 0);
                     animation.setDuration((long) (500 * Declare.speed));
                     animation.setFillAfter(false);
                 	 pointer.startAnimation(animation);
                 	 Declare.pointerInScreen = (int)(pointerInScreen - Declare.pointer_unpress/2);
                 }
                 break;
             case MOVECANVASTOSTART:
            	 drawView.reDraw();    
            	 break;
             }
             super.handleMessage(msg); 
        } 
    };

    
    private class SaveDialog {
    	int type;

		public SaveDialog(int type) {
			super();
			this.type = type;
			if (type == SAVEONLY || type == RECORDDIALOG) {
				saveStaffs();
			}
			else if (Declare.isSaved == false) {
				Dialog isSaveDialog = new AlertDialog.Builder(MainActivity.this).setMessage("是否保存一下？")
				//设置按钮
						.setPositiveButton("保存", new DialogInterface.OnClickListener(){ 
        							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								saveStaffs();
							}  
						})
						.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
					        
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Declare.isSaved = true;
								callFunction();
							}
						})
						.setCancelable(true).create();
				isSaveDialog.show();
			}
			else {	//已保存
				callFunction();
			}
		}

		//判断有没有名字，有名字就直接存，没名字就取名字
		private void saveStaffs() {

			if (fileName.length() == 0) {
				edit = new EditText(MainActivity.this);
				edit.setSingleLine();
				edit.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI|EditorInfo.IME_ACTION_DONE); 

				Dialog onSaveDialog = new AlertDialog.Builder(MainActivity.this).setTitle("保存")
						.setMessage("想一个靠谱的名字吧＝ ＝、").setView(edit)
						//设置按钮
						.setPositiveButton("保存", new DialogInterface.OnClickListener(){  
							public void onClick(DialogInterface dialog, int which) {
								fileName = edit.getText().toString();
								//Log.v("debuga", "fileName: " + fileName);
								
								if (new File(historyPath + "/" + fileName + ".psong").exists()) {
//									Log.v("debuga", "file exists()");
									AlertDialog temp = new AlertDialog.Builder(MainActivity.this)
										.setTitle("保存失败").setMessage("有重名")
										.setPositiveButton("确定", new DialogInterface.OnClickListener(){
											public void onClick(DialogInterface dialog, int which) {
												//不做任何操作
												fileName = "";
												edit.setVisibility(View.GONE);
											}
										}).create();
									temp.show();
								}
								else if (fileName.length() == 0) {
									AlertDialog temp = new AlertDialog.Builder(MainActivity.this)
									.setTitle("保存失败").setMessage("文件名为空")
									.setPositiveButton("确定", new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface dialog, int which) {
											//不做任何操作
											edit.setVisibility(View.GONE);
										}
									}).create();
								temp.show();
								}
								else {
									//保存应该做的事
									try {
										onSave();
										callFunction();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}								
								}
							}  
						})
						.setCancelable(true).create();
				
				//提醒用户做保存的事情的一个框
				onSaveDialog.show();
			}

			else {	//已有名字
				//保存应该做的事
				try {
					onSave();
					callFunction();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}    					
			}
		} 
		
		private void callFunction() {
			switch (this.type) {
			case NEWCANVASDIALOG:
				newCanvas(true);
				break;
			case IMPORTDIALOG:
				pickImage();
				break;
			case HISTORYDIALOG:
				showFileList();
				break;
			case RECORDDIALOG:
				recordSong();
				break;
			case HELPDIALOG:
				startGuide();
				break;
			case SAVEONLY:
				break;
			}
		} 	
    }
    
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) {         
    	//按下键盘上返回按钮
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
    		if (listview.getVisibility() == View.VISIBLE) {
    			listview.setVisibility(View.GONE);
    		}
    		else {
	    		new AlertDialog.Builder(this) 
	    			.setTitle("退出").setMessage("确认要退出吗？")
	    			.setNegativeButton("谁说我要退出", new DialogInterface.OnClickListener() { 
	    				@Override 
	    				public void onClick(DialogInterface dialog, int which) { 
	    				}
	    			}) 
	    			.setPositiveButton("我要走了谁也别拦我", new DialogInterface.OnClickListener() {
	    				@Override 
	    				public void onClick(DialogInterface dialog, int whichButton) { 
	    					finish();
	    				}
	    			}).show(); 
    		}
    		return true; 
    	}
    	else {   
    		return super.onKeyDown(keyCode, event);
    	}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		drawView.reDraw();
		this.getDirPath();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
		
    @Override 
    protected void onDestroy() { 
    	super.onDestroy();
    	System.exit(0);
    	//或者下面这种方式
    	//android.os.Process.killProcess(android.os.Process.myPid());
    }

	
	//LongPressButton Listener的接口
	@Override
	public void onRepeat(View v, long duration, int repeatcount) {
		duration = 500;
		repeatcount = 1;
	}
	
	@Override
	public void fastAction(int direction) {
		//传入左移或右移值
		Log.v("Long", "fastAction");
		if(drawView.melody_start > 0 || direction == 1){
			drawView.melody_start += direction * 80;
			drawView.reDraw();
		}
	}

	@Override
	public void flipAction(int direction) {
		Log.v("Long", "flipAction");
		if(drawView.melody_start > 0){
			drawView.melody_start += direction * 400;
			drawView.reDraw();
		}
	}
}
