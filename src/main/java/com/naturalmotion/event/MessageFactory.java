package com.naturalmotion.event;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.linecorp.bot.model.message.FlexMessage;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.ImageMessage.ImageMessageBuilder;
import com.linecorp.bot.model.message.flex.component.Box;
import com.linecorp.bot.model.message.flex.component.FlexComponent;
import com.linecorp.bot.model.message.flex.component.Image;
import com.linecorp.bot.model.message.flex.component.Image.ImageAspectMode;
import com.linecorp.bot.model.message.flex.component.Image.ImageSize;
import com.linecorp.bot.model.message.flex.component.Separator;
import com.linecorp.bot.model.message.flex.component.Text;
import com.linecorp.bot.model.message.flex.container.Bubble;
import com.linecorp.bot.model.message.flex.container.Bubble.BubbleSize;
import com.linecorp.bot.model.message.flex.container.BubbleStyles;
import com.linecorp.bot.model.message.flex.container.BubbleStyles.BlockStyle;
import com.linecorp.bot.model.message.flex.container.FlexContainer;
import com.linecorp.bot.model.message.flex.unit.FlexAlign;
import com.linecorp.bot.model.message.flex.unit.FlexFontSize;
import com.linecorp.bot.model.message.flex.unit.FlexLayout;
import com.linecorp.bot.model.message.flex.unit.FlexPaddingSize;
import com.naturalmotion.database.TOKEN_RARITY;
import com.naturalmotion.webservice.service.json.Card;

public class MessageFactory {

	public FlexMessage createGoldFull(String crew) throws URISyntaxException {
		return createFullCardFlexMessage(crew, TOKEN_RARITY.GOLD);
	}

	public FlexMessage createSilverFull(String crew) throws URISyntaxException {
		return createFullCardFlexMessage(crew, TOKEN_RARITY.SILVER);
	}

	public FlexMessage createBronzeFull(String crew) throws URISyntaxException {
		return createFullCardFlexMessage(crew, TOKEN_RARITY.BRONZE);
	}

	private FlexMessage createFullCardFlexMessage(String crew, TOKEN_RARITY rarity) throws URISyntaxException {
		String text = "joker " + rarity + "% plein";
		Box card = Box.builder().layout(FlexLayout.HORIZONTAL).content(
		        Text.builder().text(text).size(FlexFontSize.XXXL).align(FlexAlign.CENTER).color("#ffffff").build())
		        .build();
		return createFlexMessage(crew, card, text);
	}

	public ImageMessage createImage(Card actualCard, String crew) throws URISyntaxException {
		ImageMessageBuilder imBuilder = ImageMessage.builder();
		URI uri = new URI("https://mod.csr-lesnains.fr/csr-admin/images/line/"
		        + TOKEN_RARITY.from(actualCard.getRarity()).getName() + "_" + crew + ".jpg");
		imBuilder.originalContentUrl(uri);
		imBuilder.previewImageUrl(uri);
		return imBuilder.build();
	}

	public FlexMessage createUserTokenFlexMessage(String crew, Map<String, List<List<String>>> data)
	        throws URISyntaxException {
		return createFlexMessage(crew, userTokenBody(data), "Remplissage des jokers");
	}

	private FlexMessage createFlexMessage(String crew, Box body, String altText) throws URISyntaxException {
		FlexComponent heroComponent = Image.builder().size(ImageSize.FULL_WIDTH).aspectMode(ImageAspectMode.Cover)
		        .aspectRatio("10:2").url(new URI("https://mod.csr-lesnains.fr/csr-admin/images/line/" + crew + ".png"))
		        .build();
		FlexContainer flexContainer = Bubble.builder()
		        .styles(BubbleStyles.builder()
		                .body(BlockStyle.builder().backgroundColor("#000000").separatorColor("#ffffff").build())
		                .build())
		        .hero(heroComponent).size(BubbleSize.GIGA).body(body).build();
		return FlexMessage.builder().altText(altText).contents(flexContainer).build();
	}

	private Box userTokenBody(Map<String, List<List<String>>> data) {
		List<FlexComponent> lines = new ArrayList<>();
		Iterator<String> keys = data.keySet().iterator();
		while (keys.hasNext()) {
			String day = keys.next();

			if (data.get(day).get(0).size() > 0) {
				lines.add(createLine(day, data.get(day)));
				if (keys.hasNext()) {
					lines.add(separator());
				}
			}
		}
		Box body = null;
		if (lines.isEmpty()) {
			body = Box.builder().width("100%").paddingAll(FlexPaddingSize.LG).layout(FlexLayout.HORIZONTAL)
			        .content(Text.builder().color("#ffffff").text("Aucun jeton n'a été posé").build()).build();
		} else {
			body = Box.builder().width("100%").paddingAll(FlexPaddingSize.LG).layout(FlexLayout.VERTICAL)
			        .contents(lines).build();
		}
		return body;
	}

	private Box createLine(String day, List<List<String>> list) {
		Text cell1 = Text.builder().flex(2).text(day).color("#ffffff").build();

		List<FlexComponent> userDetails = new ArrayList<>();
		for (int i = 0; i < list.get(0).size(); i++) {
			Text cellUserName = cellCenter(list.get(0).get(i));

			Box cellTokenDetails = Box.builder().layout(FlexLayout.HORIZONTAL)
			        .contents(cellCenter(list.get(1).get(i)), cellCenter(list.get(2).get(i))).build();

			userDetails.add(cellUserName);
			userDetails.add(cellTokenDetails);
			if (i < list.get(0).size() - 1) {
				userDetails.add(separator());
			}

		}
		Box cellUserDetails = Box.builder().flex(6).layout(FlexLayout.VERTICAL).contents(userDetails).build();
		return Box.builder().paddingTop(FlexPaddingSize.LG).layout(FlexLayout.HORIZONTAL)
		        .contents(cell1, separator(), cellUserDetails).build();
	}

	private Separator separator() {
		return Separator.builder().color("#ffffff").build();
	}

	private Text cellCenter(String name) {
		return Text.builder().align(FlexAlign.CENTER).text(name).color("#ffffff").wrap(true).build();
	}

}
