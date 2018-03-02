package com.madongfang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.madongfang.api.CardApi;
import com.madongfang.api.CardRechargeApi;
import com.madongfang.entity.Manager;
import com.madongfang.service.CustomService;

@RestController
@RequestMapping(value="/api/cards")
public class CardController {

	@GetMapping(value="/{cardId}")
	public CardApi getCard(@PathVariable String cardId) {
		return customService.getCard(cardId);
	}
	
	@PostMapping(value="/{cardId}/recharge")
	public CardApi updateCard(@SessionAttribute Manager manager, @PathVariable String cardId, @RequestBody CardRechargeApi cardRechargeApi)
	{
		return customService.rechargeCard(manager.getId(), cardId, cardRechargeApi);
	}
	
	@PostMapping
	public CardApi addCard(@RequestBody CardApi cardApi) {
		return customService.addCard(cardApi);
	}
	
	@Autowired
	private CustomService customService;
}
